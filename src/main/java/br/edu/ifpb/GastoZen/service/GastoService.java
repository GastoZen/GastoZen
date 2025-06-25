package br.edu.ifpb.GastoZen.service;

import br.edu.ifpb.GastoZen.model.Gasto;
import br.edu.ifpb.GastoZen.repository.GastoRepository;
import com.google.cloud.Timestamp;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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


}