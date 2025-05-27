package br.edu.ifpb.service;

import br.edu.ifpb.model.Gasto;
import br.edu.ifpb.repository.GastoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GastoServiceTest {

    @Mock
    private GastoRepository gastoRepository;

    private GastoService gastoService;

    @BeforeEach
    void setUp() {
        gastoService = new GastoService(gastoRepository);
    }

    @Test
    void cadastrarGastoValido() throws ExecutionException, InterruptedException {
        Gasto gasto = new Gasto(
            new BigDecimal("100.00"),
            LocalDate.now(),
            "Compras do mercado"
        );

        when(gastoRepository.save(gasto)).thenReturn(gasto);

        Gasto gastoSalvo = gastoService.cadastrarGasto(gasto);

        assertNotNull(gastoSalvo);
        assertEquals(gasto.getValor(), gastoSalvo.getValor());
        assertEquals(gasto.getData(), gastoSalvo.getData());
        assertEquals(gasto.getDescricao(), gastoSalvo.getDescricao());
        verify(gastoRepository).save(gasto);
    }

    @Test
    void cadastrarGastoInvalido() {
        Gasto gasto = new Gasto();
        assertThrows(IllegalArgumentException.class, () -> gastoService.cadastrarGasto(gasto));
    }

    @Test
    void listarGastos() throws ExecutionException, InterruptedException {
        List<Gasto> gastos = Arrays.asList(
            new Gasto(new BigDecimal("100.00"), LocalDate.now(), "Gasto 1"),
            new Gasto(new BigDecimal("200.00"), LocalDate.now(), "Gasto 2")
        );

        when(gastoRepository.findAll()).thenReturn(gastos);

        List<Gasto> gastosListados = gastoService.listarGastos();

        assertNotNull(gastosListados);
        assertEquals(2, gastosListados.size());
        verify(gastoRepository).findAll();
    }
}