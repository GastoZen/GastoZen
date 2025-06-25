package br.edu.ifpb.GastoZen.dto;

import java.math.BigDecimal;

public class CategoriaTotal {
    private String categoria;
    private BigDecimal total;

    // Construtor
    public CategoriaTotal(String categoria, BigDecimal total) {
        this.categoria = categoria;
        this.total = total;
    }

    // Getters e setters
    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getTotal() {
        return total;
    }
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
