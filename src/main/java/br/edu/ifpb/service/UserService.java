package br.edu.ifpb.service;

import br.edu.ifpb.model.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {


    private static final String COLLECTION_NAME = "users";
    private final Firestore firestore;

    @Autowired
    public UserService(Firestore firestore) {
        this.firestore = firestore;
    }

    public User registerUser(User user) {
        validateUser(user);
        try {
            // Prevent duplicate by email
            DocumentReference docRef = firestore
                    .collection(COLLECTION_NAME)
                    .document(user.getEmail());
            ApiFuture<DocumentSnapshot> check = docRef.get();
            if (check.get().exists()) {
                throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
            }

            // Save new user
            ApiFuture<WriteResult> writeResult = docRef.set(user);
            writeResult.get(); // wait completion
            return user;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error registering user in Firebase", e);
        }
    }

    public Optional<User> findUserByEmail(String email) {
        try {
            DocumentReference docRef = firestore
                    .collection(COLLECTION_NAME)
                    .document(email);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            return document.exists()
                    ? Optional.of(document.toObject(User.class))
                    : Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error finding user by email in Firebase", e);
        }
    }

    public List<User> getAllUsers() {
        try {
            ApiFuture<QuerySnapshot> future = firestore
                    .collection(COLLECTION_NAME)
                    .get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            List<User> users = new ArrayList<>();
            for (QueryDocumentSnapshot doc : docs) {
                users.add(doc.toObject(User.class));
            }
            return users;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error retrieving all users from Firebase", e);
        }
    }

    public void deleteUser(String email) {
        try {
            ApiFuture<WriteResult> writeResult = firestore
                    .collection(COLLECTION_NAME)
                    .document(email)
                    .delete();
            writeResult.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error deleting user in Firebase", e);
        }
    }

    private void validateUser(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (user.getAge() <= 0) {
            throw new IllegalArgumentException("Age must be positive");
        }
        if (user.getSalary() < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be empty");
        }
        if (user.getOccupation() == null || user.getOccupation().trim().isEmpty()) {
            throw new IllegalArgumentException("Occupation cannot be empty");
        }
    }
}
