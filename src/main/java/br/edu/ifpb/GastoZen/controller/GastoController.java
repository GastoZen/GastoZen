package br.edu.ifpb.gastozen.controller;

import br.edu.ifpb.gastozen.model.Gasto;
import br.edu.ifpb.gastozen.service.GastoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @PostMapping
    public ResponseEntity<Gasto> cadastrarGasto(@RequestBody Gasto gasto) {
        try {
            Gasto novoGasto = gastoService.cadastrarGasto(gasto, getCurrentUserId());
            return new ResponseEntity<>(novoGasto, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Gasto>> listarGastos() {
        try {
            List<Gasto> gastos = gastoService.listarGastosDoUsuario(getCurrentUserId());
            return new ResponseEntity<>(gastos, HttpStatus.OK);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Gasto> buscarGasto(@PathVariable String id) {
        try {
            return gastoService.buscarGasto(id)
                    .map(gasto -> {
                        if (!gasto.getUserId().equals(getCurrentUserId())) {
                            return new ResponseEntity<Gasto>(HttpStatus.FORBIDDEN);
                        }
                        return new ResponseEntity<>(gasto, HttpStatus.OK);
                    })
                    .orElse(new ResponseEntity<Gasto>(HttpStatus.NOT_FOUND));
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Gasto> atualizarGasto(@PathVariable String id, @RequestBody Gasto gasto) {
        try {
            Gasto gastoAtualizado = gastoService.atualizarGasto(id, gasto, getCurrentUserId());
            return new ResponseEntity<>(gastoAtualizado, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarGasto(@PathVariable String id) {
        try {
            gastoService.deletarGasto(id, getCurrentUserId());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}