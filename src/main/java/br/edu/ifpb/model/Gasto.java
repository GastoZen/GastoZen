package br.edu.ifpb.model;

import java.time.LocalDate;
import java.math.BigDecimal;

public class Gasto {
    private Long id;
    private BigDecimal valor;
    private LocalDate data;
    private String descricao;

    public Gasto() {
    }

    public Gasto(BigDecimal valor, LocalDate data, String descricao) {
        this.valor = valor;
        this.data = data;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}