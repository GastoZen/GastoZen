package br.edu.ifpb.controller;

import br.edu.ifpb.model.Gasto;
import br.edu.ifpb.service.GastoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/gastos")
public class GastoController {
    private final GastoService gastoService;

    public GastoController(GastoService gastoService) {
        this.gastoService = gastoService;
    }

    @PostMapping
    public ResponseEntity<Gasto> cadastrarGasto(@RequestBody Gasto gasto) {
        try {
            Gasto gastoSalvo = gastoService.cadastrarGasto(gasto);
            return ResponseEntity.ok(gastoSalvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Gasto>> listarGastos() {
        try {
            List<Gasto> gastos = gastoService.listarGastos();
            return ResponseEntity.ok(gastos);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}