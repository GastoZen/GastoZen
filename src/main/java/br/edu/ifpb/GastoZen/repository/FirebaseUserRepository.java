package br.edu.ifpb.GastoZen.repository;

import br.edu.ifpb.GastoZen.model.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import org.springframework.stereotype.Repository;

@Repository
public class FirebaseUserRepository implements UserRepository {
    private static final String COLLECTION_NAME = "users";
    private final Firestore firestore;

    public FirebaseUserRepository() {
        this.firestore = FirestoreClient.getFirestore();
    }

    @Override
    public User save(User user) {
        try {
            if (exists(user.getEmail())) {
                throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
            }
            
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(user.getEmail());
            ApiFuture<WriteResult> result = docRef.set(user);
            result.get(); // Wait for the operation to complete
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
            
            if (document.exists()) {
                return Optional.of(document.toObject(User.class));
            }
            return Optional.empty();
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
            
            for (QueryDocumentSnapshot document : documents) {
                users.add(document.toObject(User.class));
            }
            return users;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error retrieving users", e);
        }
    }

    @Override
    public void delete(String email) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(email);
            ApiFuture<WriteResult> result = docRef.delete();
            result.get(); // Wait for the operation to complete
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error deleting user", e);
        }
    }

    @Override
    public boolean exists(String email) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(email);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            return document.exists();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error checking user existence", e);
        }
    }

    @Override
    public Optional<User> findByUid(String uid) {
        try {
            // Query users collection where uid equals the provided uid
            Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("uid", uid);
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            if (!documents.isEmpty()) {
                // Return the first user with matching uid
                return Optional.of(documents.get(0).toObject(User.class));
            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error finding user by uid", e);
        }
    }
}