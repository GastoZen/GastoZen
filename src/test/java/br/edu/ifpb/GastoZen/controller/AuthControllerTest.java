package br.edu.ifpb.GastoZen.controller;

import br.edu.ifpb.GastoZen.dto.LoginRequest;
import br.edu.ifpb.GastoZen.dto.LoginResponse;
import br.edu.ifpb.GastoZen.service.AuthService;
import com.google.firebase.auth.FirebaseAuthException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void loginSuccess() throws FirebaseAuthException {
        // Arrange
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        LoginResponse mockResponse = new LoginResponse(
                "mock-token",
                "mock-uid",
                "test@example.com",
                "Test User"
        );
        when(authService.login(any(LoginRequest.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<LoginResponse> response = authController.login(request);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("mock-token", response.getBody().getToken());
        assertEquals("mock-uid", response.getBody().getUid());
    }

    @Test
    void loginFailure() throws FirebaseAuthException {
        // Arrange
        LoginRequest request = new LoginRequest("invalid@example.com", "wrong-password");
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new FirebaseAuthException("error", "Authentication failed"));

        // Act
        ResponseEntity<LoginResponse> response = authController.login(request);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
    }
}