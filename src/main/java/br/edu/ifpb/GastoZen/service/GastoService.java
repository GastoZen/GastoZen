package br.edu.ifpb.GastoZen.service;

import br.edu.ifpb.GastoZen.model.Gasto;
import br.edu.ifpb.GastoZen.repository.GastoRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class GastoService {
    private final GastoRepository gastoRepository;

    public GastoService(GastoRepository gastoRepository) {
        this.gastoRepository = gastoRepository;
    }

    public Gasto cadastrarGasto(Gasto gasto) throws ExecutionException, InterruptedException {
        if (gasto.getValor() == null || gasto.getData() == null || gasto.getDescricao() == null) {
            throw new IllegalArgumentException("Todos os campos são obrigatórios: valor, data e descrição");
        }
        return gastoRepository.save(gasto);
    }

    public List<Gasto> listarGastos() throws ExecutionException, InterruptedException {
        return gastoRepository.findAll();
    }
}