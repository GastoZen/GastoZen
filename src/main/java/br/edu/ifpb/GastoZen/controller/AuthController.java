package br.edu.ifpb.GastoZen.controller;

import br.edu.ifpb.GastoZen.dto.LoginRequest;
import br.edu.ifpb.GastoZen.dto.LoginResponse;
import br.edu.ifpb.GastoZen.model.User;
import br.edu.ifpb.GastoZen.service.AuthService;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        try {
            String uid = (String) authentication.getPrincipal();
            User user = authService.getCurrentUser(uid);
            return ResponseEntity.ok(user);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}