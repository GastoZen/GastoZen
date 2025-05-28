package br.edu.ifpb.service;

import br.edu.ifpb.model.Gasto;
import br.edu.ifpb.repository.GastoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class GastoService {
    private final GastoRepository gastoRepository;

    @Autowired
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
