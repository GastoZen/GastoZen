package br.edu.ifpb.GastoZen.controller;

import br.edu.ifpb.GastoZen.model.Gasto;
import br.edu.ifpb.GastoZen.service.GastoService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/gastos")
public class GastoController {
    private final GastoService gastoService;

    public GastoController(GastoService gastoService) {
        this.gastoService = gastoService;
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @PostMapping
    public ResponseEntity<?> cadastrarGasto(
            @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
            @RequestBody Gasto gasto) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token não fornecido ou mal formatado.");
        }

        String idToken = authorizationHeader.substring(7);
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String userId = decodedToken.getUid();

            Gasto gastoSalvo = gastoService.cadastrarGasto(gasto, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(gastoSalvo);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro de validação: " + e.getMessage());
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao cadastrar gasto: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido: " + e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<List<Gasto>> listarGastos() {
        try {
            List<Gasto> gastos = gastoService.listarGastosDoUsuario(getCurrentUserId());
            return new ResponseEntity<>(gastos, HttpStatus.OK);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Gasto> buscarGasto(@PathVariable String id) {
        try {
            return gastoService.buscarGasto(id)
                    .map(gasto -> {
                        if (!gasto.getUserId().equals(getCurrentUserId())) {
                            return new ResponseEntity<Gasto>(HttpStatus.FORBIDDEN);
                        }
                        return new ResponseEntity<>(gasto, HttpStatus.OK);
                    })
                    .orElse(new ResponseEntity<Gasto>(HttpStatus.NOT_FOUND));
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarGasto(
            @PathVariable String id,
            @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
            @RequestBody Gasto gasto) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token não fornecido ou mal formatado.");
        }

        String idToken = authorizationHeader.substring(7);
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String userId = decodedToken.getUid();

            Gasto gastoAtualizado = gastoService.atualizarGasto(id, gasto, userId);
            return ResponseEntity.ok(gastoAtualizado);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro de validação: " + e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuário não autorizado");
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar gasto: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarGasto(@PathVariable String id) {
        try {
            gastoService.deletarGasto(id, getCurrentUserId());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}