package br.edu.ifpb.GastoZen.repository;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import br.edu.ifpb.GastoZen.model.Gasto;

import com.google.api.core.ApiFuture;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class GastoRepository {
    private final Firestore firestore;
    private static final String COLLECTION_NAME = "gastos";

    public GastoRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public Gasto save(Gasto gasto) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document();
        ApiFuture<WriteResult> future = docRef.set(gasto);
        future.get(); // Wait for the operation to complete
        gasto.setId(generateId());
        return gasto;
    }
    
    private Long generateId() {
        return System.currentTimeMillis();
    }

    public List<Gasto> findAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        QuerySnapshot querySnapshot = future.get();
        return querySnapshot.getDocuments().stream()
                .map(document -> document.toObject(Gasto.class))
                .collect(Collectors.toList());
    }
}