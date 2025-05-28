package br.edu.ifpb.GastoZen.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.Timestamp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class Gasto {
    @DocumentId
    private String id;
    private String userId;  // Reference to the user who created the expense
    private BigDecimal valor;
    private Timestamp data;  // Using Firestore Timestamp
    private String descricao;
    private String categoria;  // Optional category field

    public Gasto() {
        // Required for Firestore
    }

    public Gasto(String userId, BigDecimal valor, LocalDate data, String descricao, String categoria) {
        this.userId = userId;
        this.valor = valor;
        this.data = Timestamp.of(Date.from(data.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        this.descricao = descricao;
        this.categoria = categoria;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getData() {
        return data.toDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public void setData(LocalDate data) {
        this.data = Timestamp.of(Date.from(data.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    public Timestamp getDataTimestamp() {
        return data;
    }

    public void setDataTimestamp(Timestamp data) {
        this.data = data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}