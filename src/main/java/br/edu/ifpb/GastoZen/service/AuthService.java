package br.edu.ifpb.GastoZen.service;

import br.edu.ifpb.GastoZen.dto.LoginRequest;
import br.edu.ifpb.GastoZen.dto.LoginResponse;
import br.edu.ifpb.GastoZen.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public LoginResponse login(LoginRequest request) throws FirebaseAuthException {
        try {
            UserRecord userRecord = FirebaseAuth.getInstance()
                    .getUserByEmail(request.getEmail());
            
            // Note: In a real application, you would verify the password here
            // Firebase Admin SDK doesn't support direct email/password authentication
            // You would typically use Firebase Client SDK for this
            
            String customToken = FirebaseAuth.getInstance()
                    .createCustomToken(userRecord.getUid());

            return new LoginResponse(
                    customToken,
                    userRecord.getUid(),
                    userRecord.getEmail(),
                    userRecord.getDisplayName()
            );
        } catch (FirebaseAuthException e) {
            throw new FirebaseAuthException(e.getErrorCode().toString(), "Authentication failed: " + e.getMessage());
        }
    }

    public User getCurrentUser(String uid) throws FirebaseAuthException {
        UserRecord userRecord = FirebaseAuth.getInstance().getUser(uid);
        return new User(
                userRecord.getUid(),
                userRecord.getEmail(),
                userRecord.getDisplayName()
        );
    }
}