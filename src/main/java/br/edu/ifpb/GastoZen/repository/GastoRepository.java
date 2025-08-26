package br.edu.ifpb.GastoZen.repository;

import br.edu.ifpb.GastoZen.model.Gasto;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
public class GastoRepository {
    private static final String COLLECTION_NAME = "gastos";
    private final Firestore firestore;

    public GastoRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public Gasto save(Gasto gasto) throws ExecutionException, InterruptedException {
        if (gasto.getId() == null) {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document();
            gasto.setId(docRef.getId());
            ApiFuture<WriteResult> future = docRef.set(gasto);
            future.get();
        } else {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(gasto.getId());
            ApiFuture<WriteResult> future = docRef.set(gasto);
            future.get();
        }
        return gasto;
    }

    public Optional<Gasto> findById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return Optional.ofNullable(document.toObject(Gasto.class));
        }
        return Optional.empty();
    }

    public List<Gasto> findByUserId(String userId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .get();

        List<Gasto> gastos = new ArrayList<>();
        QuerySnapshot querySnapshot = future.get();

        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            gastos.add(document.toObject(Gasto.class));
        }

        return gastos;
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<WriteResult> future = docRef.delete();
        future.get();
    }

    public List<Gasto> findAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();

        List<Gasto> gastos = new ArrayList<>();
        QuerySnapshot querySnapshot = future.get();

        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            gastos.add(document.toObject(Gasto.class));
        }
        return gastos;
    }
}