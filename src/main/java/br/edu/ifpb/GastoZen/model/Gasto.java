package br.edu.ifpb.GastoZen.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Gasto {
    @DocumentId
    private String id;
    private String userId;
    private BigDecimal valor;
    private Timestamp data;
    private String descricao;
    private String categoria;

    public Gasto(String userId, BigDecimal valor, LocalDate data, String descricao, String categoria) {
        this.userId = userId;
        this.valor = valor;
        this.data = Timestamp.of(Date.from(data.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        this.descricao = descricao;
        this.categoria = categoria;
    }
}