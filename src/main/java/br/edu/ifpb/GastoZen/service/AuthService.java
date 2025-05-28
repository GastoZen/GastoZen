package br.edu.ifpb.gastozen.service;

import br.edu.ifpb.gastozen.dto.LoginRequest;
import br.edu.ifpb.gastozen.dto.LoginResponse;
import br.edu.ifpb.gastozen.dto.RegisterRequest;
import br.edu.ifpb.gastozen.model.User;
import br.edu.ifpb.gastozen.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public LoginResponse login(LoginRequest request) throws FirebaseAuthException {
        try {
            UserRecord userRecord = FirebaseAuth.getInstance()
                    .getUserByEmail(request.getEmail());

            String customToken = FirebaseAuth.getInstance()
                    .createCustomToken(userRecord.getUid());

            Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

            if (userOptional.isEmpty()) {
                User newUser = new User(
                        userRecord.getUid(),
                        userRecord.getEmail(),
                        userRecord.getDisplayName() != null ? userRecord.getDisplayName() : ""
                );
                userRepository.save(newUser);

                return new LoginResponse(
                        customToken,
                        newUser.getUid(),
                        newUser.getEmail(),
                        newUser.getName()
                );
            }

            User user = userOptional.get();

            return new LoginResponse(
                    customToken,
                    user.getUid(),
                    user.getEmail(),
                    user.getName()
            );
        } catch (FirebaseAuthException e) {
            throw new FirebaseAuthException(e.getErrorCode(), "Authentication failed: " + e.getMessage(), e, null, null);
        }
    }

    public User getCurrentUser(String uid) throws FirebaseAuthException {
        try {
            UserRecord userRecord = FirebaseAuth.getInstance().getUser(uid);
            Optional<User> userOptional = userRepository.findByUid(userRecord.getUid());

            return userOptional.orElseGet(() -> new User(
                    userRecord.getUid(),
                    userRecord.getEmail(),
                    userRecord.getDisplayName()
            ));
        } catch (FirebaseAuthException e) {
            throw new FirebaseAuthException(e.getErrorCode(), "Failed to get user: " + e.getMessage(), e, null, null);
        }
    }

    public User register(RegisterRequest request) throws FirebaseAuthException {
        validateUserData(request);

        try {
            if (userRepository.exists(request.getEmail())) {
                throw new IllegalArgumentException("User with this email already exists");
            }

            CreateRequest createRequest = new CreateRequest()
                    .setEmail(request.getEmail())
                    .setPassword(request.getPassword())
                    .setDisplayName(request.getName())
                    .setEmailVerified(false);

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(createRequest);

            User user = new User(
                    userRecord.getUid(),
                    userRecord.getEmail(),
                    request.getName(),
                    request.getAge(),
                    request.getSalary(),
                    request.getPhone(),
                    request.getOccupation()
            );

            userRepository.save(user);

            return user;
        } catch (FirebaseAuthException e) {
            throw new FirebaseAuthException(e.getErrorCode(), "Registration failed: " + e.getMessage(), e, null, null);
        }
    }

    private void validateUserData(RegisterRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (request.getAge() <= 0) {
            throw new IllegalArgumentException("Age must be positive");
        }
        if (request.getSalary() < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
        if (request.getEmail() == null || !request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be empty");
        }
        if (request.getOccupation() == null || request.getOccupation().trim().isEmpty()) {
            throw new IllegalArgumentException("Occupation cannot be empty");
        }
    }
}