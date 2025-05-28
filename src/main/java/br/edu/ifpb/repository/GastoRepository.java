package br.edu.ifpb.repository;

import br.edu.ifpb.model.Gasto;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.firestore.DocumentReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class GastoRepository {
    private static final String COLLECTION_NAME = "gastos";
    private final Firestore firestore;

    @Autowired
    public GastoRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public Gasto save(Gasto gasto) throws ExecutionException, InterruptedException {
        long id = generateId();
        // Cria o documento com o ID numérico convertido em String
        DocumentReference docRef = firestore
                .collection(COLLECTION_NAME)
                .document(String.valueOf(id));

        ApiFuture<WriteResult> future = docRef.set(gasto);
        future.get(); // espera completar

        // Agora o seu modelo aceita esse Long
        gasto.setId(id);
        return gasto;
    }

    private long generateId() {
        return System.currentTimeMillis();
    }

    public List<Gasto> findAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        return future.get().getDocuments()
                .stream()
                .map(d -> d.toObject(Gasto.class))
                .collect(Collectors.toList());
    }
}