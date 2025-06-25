package br.edu.ifpb.GastoZen.controller;

import br.edu.ifpb.GastoZen.dto.CategoriaTotal;
import br.edu.ifpb.GastoZen.service.GastoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/relatorio")
@CrossOrigin(origins = "*")
public class RelatorioController {

    private final GastoService gastoService;

    @Autowired
    public RelatorioController(GastoService gastoService) {
        this.gastoService = gastoService;
    }

    /**
     * Retorna total de gastos por categoria para o usuário autenticado.
     * Usa o Authentication.principal como UID (conforme AuthController).
     */
    @GetMapping("/categorias")
    public List<CategoriaTotal> porCategoria(Authentication authentication)
            throws Exception {

        // 1) Extrai o UID do Authentication (conforme implementado no AuthController /me)
        String uid = (String) authentication.getPrincipal();

        // 2) Usa o service para agrupar por categoria
        Map<String, BigDecimal> mapa = gastoService.calcularRankingPorCategoria(uid);

        // 3) Converte para DTO
        return mapa.entrySet().stream()
                .map(e -> new CategoriaTotal(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
