package br.edu.ifpb.GastoZen.service;

import br.edu.ifpb.GastoZen.model.Gasto;
import br.edu.ifpb.GastoZen.repository.GastoRepository;
import com.google.cloud.Timestamp;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

@Service
public class GastoService {
    private final GastoRepository gastoRepository;

    public GastoService(GastoRepository gastoRepository) {
        this.gastoRepository = gastoRepository;
    }

    public Gasto cadastrarGasto(Gasto gasto, String userId) throws ExecutionException, InterruptedException {
        validateGasto(gasto);
        gasto.setUserId(userId);  // Ensure the expense is associated with the user

        return gastoRepository.save(gasto);
    }

    public List<Gasto> listarGastosDoUsuario(String userId) throws ExecutionException, InterruptedException {
        return gastoRepository.findByUserId(userId);
    }

    public Optional<Gasto> buscarGasto(String id) throws ExecutionException, InterruptedException {
        return gastoRepository.findById(id);
    }

    public void deletarGasto(String id, String userId) throws ExecutionException, InterruptedException {
        Optional<Gasto> gasto = gastoRepository.findById(id);
        if (gasto.isPresent()) {
            // Verify that the user owns this expense
            if (!gasto.get().getUserId().equals(userId)) {
                throw new IllegalStateException("User not authorized to delete this expense");
            }
            gastoRepository.delete(id);
        }
    }

    public Gasto atualizarGasto(String id, Gasto gasto, String userId) throws ExecutionException, InterruptedException {
        Optional<Gasto> existingGasto = gastoRepository.findById(id);
        if (existingGasto.isPresent()) {
            // Verify that the user owns this expense
            if (!existingGasto.get().getUserId().equals(userId)) {
                throw new IllegalStateException("User not authorized to update this expense");
            }
            validateGasto(gasto);
            gasto.setId(id);
            gasto.setUserId(userId);  // Ensure the userId remains the same
            return gastoRepository.save(gasto);
        }
        throw new IllegalArgumentException("Expense not found with id: " + id);
    }

    private void validateGasto(Gasto gasto) {
        if (gasto.getValor() == null) {
            throw new IllegalArgumentException("O valor do gasto é obrigatório");
        }
        if (gasto.getData() == null) {
            throw new IllegalArgumentException("A data do gasto é obrigatória");
        }
        if (gasto.getDescricao() == null || gasto.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição do gasto é obrigatória");
        }
        if (gasto.getValor().signum() <= 0) {
            throw new IllegalArgumentException("O valor do gasto deve ser maior que zero");
        }
        
        // Validar data
        try {
            LocalDate dataGasto = LocalDate.parse(gasto.getData(), DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate hoje = LocalDate.now();
            
            if (dataGasto.isAfter(hoje)) {
                throw new IllegalArgumentException("A data do gasto não pode ser futura");
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de data inválido. Use o formato YYYY-MM-DD");
        }
    }
//novo
    public Map<String, BigDecimal> calcularRankingPorCategoria(String userId) throws ExecutionException, InterruptedException {
        List<Gasto> gastos = gastoRepository.findByUserId(userId);

        return gastos.stream()
                .collect(Collectors.groupingBy(
                        Gasto::getCategoria,
                        Collectors.reducing(BigDecimal.ZERO, Gasto::getValor, BigDecimal::add)
                ));
    }

    public int importarGastos(MultipartFile arquivo, String userId, String banco, String formato)
            throws IOException, ExecutionException, InterruptedException {
        if ("pdf".equalsIgnoreCase(formato)) {
            return importarGastosPDF(arquivo, userId, banco);
        }
        return importarGastosCSV(arquivo, userId, banco);

    }

    public int importarGastosCSV(MultipartFile arquivo, String userId, String banco)
            throws IOException, ExecutionException, InterruptedException {

        // Lê o arquivo usando UTF-8 (para evitar problemas de acentuação)
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(arquivo.getInputStream(), StandardCharsets.UTF_8));
        List<String> linhas = reader.lines().toList();

        if (linhas.size() <= 2) return 0; // sem transações

        // A segunda linha (índice 1) é o cabeçalho, dados começam na linha 2
        List<String> dados = linhas.subList(2, linhas.size());

        int count = 0;
        DateTimeFormatter formatterEntrada = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        for (String linha : dados) {
            String[] colunas = linha.split(";");
            if (colunas.length < 2) continue;

            String dataStr = colunas[0].trim();          // RELEASE_DATE
            String descricao = colunas[1].trim();        // TRANSACTION_TYPE

            // Normaliza encoding (opcional: remove caracteres estranhos)
            descricao = new String(descricao.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

            // Filtrar apenas tipos que são gastos
            if (!(descricao.startsWith("Pagamento com QR Pix")
                    || descricao.startsWith("Transferência Pix enviada") // já decodificado corretamente
                    || descricao.startsWith("TransferÃªncia Pix enviada") // fallback caso encoding venha quebrado
                    || descricao.startsWith("Pagamento")
                    || descricao.startsWith("Pagamento de contas"))) {
                continue; // ignora outras transações (ex.: recebimentos, rendimentos)
            }

            // Campo de valor: normalmente o penúltimo ou último campo, vamos assumir penúltimo (débitos)
            String valorStr = colunas[colunas.length - 2].trim().replace(",", ".");
            if (valorStr.isEmpty()) continue;

            try {
                BigDecimal valor = new BigDecimal(valorStr.replaceAll("[^0-9.-]", "")); // remove caracteres não numéricos
                valor = valor.abs(); // registra sempre positivo

                // Converter data para ISO yyyy-MM-dd
                LocalDate dataFormatada = LocalDate.parse(dataStr, formatterEntrada);

                Gasto gasto = new Gasto(userId, valor, dataFormatada.toString(), descricao, "Outros");
                cadastrarGasto(gasto, userId);
                count++;
            } catch (Exception e) {
                // ignora linha inválida, mas logaria se necessário
            }
        }

        return count;
    }

    public int importarGastosPDF(MultipartFile arquivo, String userId, String banco)
            throws IOException, ExecutionException, InterruptedException {

        // Lê o PDF
        PDDocument document = PDDocument.load(arquivo.getInputStream());
        PDFTextStripper stripper = new PDFTextStripper();
        String texto = stripper.getText(document);
        document.close();

        // Quebra o conteúdo em linhas
        List<String> linhas = texto.lines().toList();

        if (linhas.size() <= 2) return 0; // sem transações

        // Assume que a primeira ou segunda linha seja cabeçalho, como no CSV
        List<String> dados = linhas.subList(2, linhas.size());

        int count = 0;
        DateTimeFormatter formatterEntrada = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        for (String linha : dados) {
            // No PDF pode estar separado por múltiplos espaços, então usamos regex
            String[] colunas = linha.split("\\s{2,}");
            if (colunas.length < 2) continue;

            String dataStr = colunas[0].trim();       // Data
            String descricao = colunas[1].trim();     // Descrição

            // Normaliza encoding
            descricao = new String(descricao.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

            // Filtrar apenas tipos que são gastos
            if (!(descricao.startsWith("Pagamento com QR Pix")
                    || descricao.startsWith("Transferência Pix enviada")
                    || descricao.startsWith("Pagamento")
                    || descricao.startsWith("Pagamento de contas"))) {
                continue; // ignora outras transações
            }

            // O valor no PDF pode ser a última coluna
            String valorStr = colunas[colunas.length - 1].trim().replace(",", ".");
            if (valorStr.isEmpty()) continue;

            try {
                BigDecimal valor = new BigDecimal(valorStr.replaceAll("[^0-9.-]", ""));
                valor = valor.abs();

                // Converter data para ISO yyyy-MM-dd
                LocalDate dataFormatada = LocalDate.parse(dataStr, formatterEntrada);

                Gasto gasto = new Gasto(userId, valor, dataFormatada.toString(), descricao, "Outros");
                cadastrarGasto(gasto, userId);
                count++;
            } catch (Exception e) {
                // ignora linha inválida
            }
        }

        return count;
    }



}