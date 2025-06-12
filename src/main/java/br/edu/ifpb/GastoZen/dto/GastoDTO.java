package br.edu.ifpb.GastoZen.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GastoDTO {
    private String descricao;
    private BigDecimal valor;
    private String data;     // 👈 como String para evitar erro de serialização
    private String categoria;
}
