package br.edu.ifpb.GastoZen.controller;

import br.edu.ifpb.GastoZen.service.GastoService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/importacao")
@CrossOrigin(origins = "*")
public class ImportacaoController {

    private final GastoService gastoService;

    public ImportacaoController(GastoService gastoService) {
        this.gastoService = gastoService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadArquivo(
            @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam("banco") String banco,
            @RequestParam("formato") String formato) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token não fornecido ou mal formatado.");
        }

        if (arquivo.isEmpty()) {
            return ResponseEntity.badRequest().body("Nenhum arquivo enviado.");
        }

        try {
            String idToken = authorizationHeader.substring(7);
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String userId = decodedToken.getUid();

             // Chama o service que trata CSV e PDF
            int qtdImportada = gastoService.importarGastos(arquivo, userId, banco, formato);

            return ResponseEntity.ok("Importação concluída com sucesso! " + qtdImportada + " registros importados.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar arquivo: " + e.getMessage());
        }
    }
}
