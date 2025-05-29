package br.edu.ifpb.GastoZen.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/protegido")
public class RecursoProtegidoController {

    @GetMapping("/dados")
    public ResponseEntity<?> getDadosProtegidos(
            @RequestHeader(name = "Authorization", required = false) String authorizationHeader) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token não fornecido ou mal formatado.");
        }

        String idToken = authorizationHeader.substring(7);
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            return ResponseEntity.ok("Olá, " + decodedToken.getEmail() + " (uid=" + decodedToken.getUid() + ")");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido: " + ex.getMessage());
        }
    }
}
