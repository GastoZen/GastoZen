package br.edu.ifpb.repository;

import br.edu.ifpb.model.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
public class FirebaseUserRepository implements UserRepository {
    private static final String COLLECTION_NAME = "users";
    private final Firestore firestore;

    @Autowired
    public FirebaseUserRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public User save(User user) {
        try {
            if (exists(user.getEmail())) {
                throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
            }

            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(user.getEmail());
            ApiFuture<WriteResult> result = docRef.set(user);
            result.get(); // Wait for completion
            return user;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error saving user", e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(email);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            return document.exists() ? Optional.of(document.toObject(User.class)) : Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error finding user", e);
        }
    }

    @Override
    public List<User> findAll() {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            List<User> users = new ArrayList<>();
            for (QueryDocumentSnapshot doc : documents) {
                users.add(doc.toObject(User.class));
            }
            return users;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error retrieving users", e);
        }
    }

    @Override
    public void delete(String email) {
        try {
            ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME).document(email).delete();
            result.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error deleting user", e);
        }
    }

    @Override
    public boolean exists(String email) {
        try {
            ApiFuture<DocumentSnapshot> future = firestore.collection(COLLECTION_NAME).document(email).get();
            return future.get().exists();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error checking existence", e);
        }
    }
}
