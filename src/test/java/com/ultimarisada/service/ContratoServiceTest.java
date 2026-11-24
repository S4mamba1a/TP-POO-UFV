package com.ultimarisada.service;

import com.ultimarisada.exceptions.ContratoInvalidoException;
import com.ultimarisada.exceptions.OperacaoNaoPermitidaException;
import com.ultimarisada.model.Avaliacao;
import com.ultimarisada.model.Cliente;
import com.ultimarisada.model.Contratante;
import com.ultimarisada.model.Contrato;
import com.ultimarisada.model.InstrumentoDeRisada;
import com.ultimarisada.model.Palhaco;
import com.ultimarisada.model.StatusContrato;
import com.ultimarisada.repository.ClienteRepository;
import com.ultimarisada.repository.ContratanteRepository;
import com.ultimarisada.repository.ContratoRepository;
import com.ultimarisada.repository.InstrumentoRepository;
import com.ultimarisada.repository.PalhacoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ContratoServiceTest {
    private ContratoService service;
    private ContratoRepository contratoRepository;
    private ContratanteRepository contratanteRepository;
    private ClienteRepository clienteRepository;
    private PalhacoRepository palhacoRepository;
    private InstrumentoRepository instrumentoRepository;

    private LocalDate dataFinal;
    private StatusContrato status;
    private Contratante contratante;
    private Cliente cliente;
    private Palhaco palhaco;
    private InstrumentoDeRisada ins;

    @BeforeEach
    void configurarVariaveis() throws Exception {
        service = new ContratoService();
        contratoRepository = mock(ContratoRepository.class);
        contratanteRepository = mock(ContratanteRepository.class);
        clienteRepository = mock(ClienteRepository.class);
        palhacoRepository = mock(PalhacoRepository.class);
        instrumentoRepository = mock(InstrumentoRepository.class);

        Field fContrato = ContratoService.class.getDeclaredField("contratoRepository");
        fContrato.setAccessible(true);
        fContrato.set(service, contratoRepository);

        Field fContratante = ContratoService.class.getDeclaredField("contratanteRepository");
        fContratante.setAccessible(true);
        fContratante.set(service, contratanteRepository);

        Field fCliente = ContratoService.class.getDeclaredField("clienteRepository");
        fCliente.setAccessible(true);
        fCliente.set(service, clienteRepository);

        Field fPalhaco = ContratoService.class.getDeclaredField("palhacoRepository");
        fPalhaco.setAccessible(true);
        fPalhaco.set(service, palhacoRepository);

        Field fInstrumento = ContratoService.class.getDeclaredField("instrumentoRepository");
        fInstrumento.setAccessible(true);
        fInstrumento.set(service, instrumentoRepository);

        // Inicialização das variáveis do teste:
        dataFinal = LocalDate.now().plusDays(5);
        status = StatusContrato.PENDENTE;
        contratante = new Contratante(1, "José", LocalDate.now(), "1234");
        cliente = new Cliente(1, "Joaquim", LocalDate.now(), "Homem alto de pele clara", "Rua Florestal, número 123", "Mercado", contratante);
        palhaco = new Palhaco(1, "Penny", LocalDate.now(), "Esquentadinho", 1890, 4.5);
        ins = new InstrumentoDeRisada(1, 300, "Espada de Balão", "Corta até vento!");
    }

    @Test
    @Order(1)
    //Deve criar um contrato PENDENTE e salvá-lo quando todos os dados forem válidos.
    void testSolicitarSucesso() throws Exception {
        when(contratanteRepository.buscarPorId(1)).thenReturn(contratante);
        when(clienteRepository.buscarPorId(1)).thenReturn(cliente);
        when(palhacoRepository.buscarPorId(1)).thenReturn(palhaco);
        when(instrumentoRepository.buscarPorId(1)).thenReturn(ins);

        Contrato contratoCriado = service.solicitar(1, 1, 1, 1, dataFinal);

        assertNotNull(contratoCriado);
        assertEquals(StatusContrato.PENDENTE, contratoCriado.getStatus());
        assertEquals(contratante, contratoCriado.getContratante());
        assertEquals(cliente, contratoCriado.getCliente());
        assertEquals(palhaco, contratoCriado.getPalhaco());
        assertEquals(ins, contratoCriado.getInstrumento());
        assertEquals(dataFinal, contratoCriado.getDataFinal());
        verify(contratoRepository, times(1)).salvar(contratoCriado);
    }

    @Test
    @Order(2)
    //Deve lançar ContratoInvalidoException ao solicitar contrato com entidade obrigatória ou data nula.
    void testSolicitarEntidadeOuDataNula() throws Exception {
        when(contratanteRepository.buscarPorId(1)).thenReturn(null);

        assertThrows(ContratoInvalidoException.class, () -> service.solicitar(1, 1, 1, 1, dataFinal));
        verify(contratoRepository, never()).salvar(any());
    }

    @Test
    @Order(3)
    //Deve lançar ContratoInvalidoException ao solicitar contrato com cliente de outro contratante.
    void testSolicitarClienteDeOutroContratante() throws Exception {
        Contratante contratante2 = new Contratante(2, "Almiro", LocalDate.now(), "1234");
        Cliente clienteDeOutro = new Cliente(2, "Samuel", dataFinal, "Homem alto de cabelo cacheado", "Rua Pipipipopopo, número 67", "Marte", contratante2);
        when(contratanteRepository.buscarPorId(1)).thenReturn(contratante);
        when(clienteRepository.buscarPorId(2)).thenReturn(clienteDeOutro);
        when(palhacoRepository.buscarPorId(1)).thenReturn(palhaco);
        when(instrumentoRepository.buscarPorId(1)).thenReturn(ins);

        assertThrows(ContratoInvalidoException.class, () -> service.solicitar(1, 2, 1, 1, dataFinal));
        verify(contratoRepository, never()).salvar(any());
    }

    @Test
    @Order(4)
    //Deve lançar ContratoInvalidoException ao solicitar contrato com data final anterior à data atual.
    void testSolicitarDataInvalida() throws Exception {
        when(contratanteRepository.buscarPorId(1)).thenReturn(contratante);
        when(clienteRepository.buscarPorId(1)).thenReturn(cliente);
        when(palhacoRepository.buscarPorId(1)).thenReturn(palhaco);
        when(instrumentoRepository.buscarPorId(1)).thenReturn(ins);

        assertThrows(ContratoInvalidoException.class, () -> service.solicitar(1, 1, 1, 1, LocalDate.now().minusDays(1)));
        verify(contratoRepository, never()).salvar(any());
    }

    @Test
    @Order(5)
    //Deve propagar SQLException ao solicitar contrato quando ocorrer falha no banco de dados.
    void testSolicitarSqlException() throws Exception {
        when(contratanteRepository.buscarPorId(1)).thenThrow(new SQLException("Falha de conexão"));

        assertThrows(SQLException.class, () -> service.solicitar(1, 1, 1, 1, dataFinal));
    }

    @Test
    @Order(6)
    //Deve buscar e retornar um contrato por id quando ele existir.
    void testBuscarPorId() throws Exception {
        Contrato contrato = new Contrato(10, dataFinal, status, contratante, cliente, palhaco, ins, null);
        when(contratoRepository.buscarPorId(10)).thenReturn(contrato);

        Contrato contratoBuscado = service.buscarPorId(10);
        assertNotNull(contratoBuscado);
        assertEquals(10, contratoBuscado.getId());
    }

    @Test
    @Order(7)
    //Deve retornar null ao buscar um contrato inexistente por id.
    void testBuscarPorIdNull() throws Exception {
        when(contratoRepository.buscarPorId(404)).thenReturn(null);

        assertNull(service.buscarPorId(404));
    }

    @Test
    @Order(8)
    //Deve propagar SQLException ao buscar contrato por id quando ocorrer falha no banco de dados.
    void testBuscarPorIdSqlException() throws Exception {
        when(contratoRepository.buscarPorId(5)).thenThrow(new SQLException("Erro ao buscar"));

        assertThrows(SQLException.class, () -> service.buscarPorId(5));
    }

    @Test
    @Order(9)
    //Deve listar corretamente todos os contratos cadastrados.
    void testListarTodos() throws Exception {
        Contrato contrato = new Contrato(1, dataFinal, status, contratante, cliente, palhaco, ins, null);
        when(contratoRepository.listarTodos()).thenReturn(List.of(contrato));

        assertEquals(1, service.listarTodos().size());
    }

    @Test
    @Order(10)
    //Deve retornar lista vazia quando não houver contratos cadastrados.
    void testListarTodosVazio() throws Exception {
        when(contratoRepository.listarTodos()).thenReturn(List.of());

        assertTrue(service.listarTodos().isEmpty());
    }

    @Test
    @Order(11)
    //Deve propagar SQLException ao listar todos os contratos quando ocorrer falha no banco de dados.
    void testListarTodosSqlException() throws Exception {
        when(contratoRepository.listarTodos()).thenThrow(new SQLException("Erro ao listar todos"));

        assertThrows(SQLException.class, () -> service.listarTodos());
    }

    @Test
    @Order(12)
    //Deve listar corretamente todos os contratos de um contratante.
    void testListarPorContratante() throws Exception {
        Contrato contrato = new Contrato(1, dataFinal, status, contratante, cliente, palhaco, ins, null);
        when(contratoRepository.listarPorContratante(1)).thenReturn(List.of(contrato));

        assertEquals(1, service.listarPorContratante(1).size());
    }

    @Test
    @Order(13)
    //Deve retornar lista vazia ao listar contratos de um contratante sem contratos.
    void testListarPorContratanteVazio() throws Exception {
        when(contratoRepository.listarPorContratante(100)).thenReturn(List.of());

        assertTrue(service.listarPorContratante(100).isEmpty());
    }

    @Test
    @Order(14)
    //Deve propagar SQLException ao listar contratos por contratante quando ocorrer falha no banco de dados.
    void testListarPorContratanteSqlException() throws Exception {
        when(contratoRepository.listarPorContratante(3)).thenThrow(new SQLException("Erro ao listar"));

        assertThrows(SQLException.class, () -> service.listarPorContratante(3));
    }

    @Test
    @Order(15)
    //Deve calcular o valor total somando o cachê do palhaço e o valor do instrumento.
    void testCalcularValorTotal() {
        Contrato contrato = new Contrato(dataFinal, status, contratante, cliente, palhaco, ins);

        assertEquals(ins.getValor() + palhaco.getCache(), service.calcularValorTotal(contrato));
    }

    @Test
    @Order(16)
    //Deve atualizar os dados de um contrato mantendo status e avaliação originais.
    void testEditar() throws Exception {
        Contratante contratante2 = new Contratante(2, "Almiro", LocalDate.now(), "1234");
        Cliente cliente2 = new Cliente(2, "Samuel", dataFinal, "Homem alto de cabelo cacheado", "Rua Pipipipopopo, número 67", "Marte", contratante2);
        Palhaco palhaco2 = new Palhaco(2, "Robertinho", LocalDate.now(), "Explosivo", 9000, 5.0);
        InstrumentoDeRisada ins2 = new InstrumentoDeRisada(2, 700, "Buzina de Carro em Alta Velocidade", "Te mata de rir com tanto barulho!");
        Avaliacao avaliacaoOriginal = new Avaliacao(1, 5, "Serviço muito bom!");
        LocalDate novaData = LocalDate.of(2026, 12, 1);

        Contrato contrato = new Contrato(1, dataFinal, StatusContrato.REALIZADO, contratante, cliente, palhaco, ins, avaliacaoOriginal);
        when(contratoRepository.buscarPorId(1)).thenReturn(contrato);
        when(contratanteRepository.buscarPorId(2)).thenReturn(contratante2);
        when(clienteRepository.buscarPorId(2)).thenReturn(cliente2);
        when(palhacoRepository.buscarPorId(2)).thenReturn(palhaco2);
        when(instrumentoRepository.buscarPorId(2)).thenReturn(ins2);

        service.editar(1, 2, 2, 2, 2, novaData);

        assertEquals(novaData, contrato.getDataFinal());
        assertEquals(contratante2.getId(), contrato.getContratante().getId());
        assertEquals(cliente2.getId(), contrato.getCliente().getId());
        assertEquals(palhaco2.getId(), contrato.getPalhaco().getId());
        assertEquals(ins2.getId(), contrato.getInstrumento().getId());
        assertEquals(StatusContrato.REALIZADO, contrato.getStatus());
        assertEquals(avaliacaoOriginal, contrato.getAvaliacao());
        verify(contratoRepository, times(1)).atualizar(contrato);
    }

    @Test
    @Order(17)
    //Deve lançar ContratoInvalidoException ao tentar editar um contrato inexistente.
    void testEditarContratoNaoEncontrado() throws Exception {
        when(contratoRepository.buscarPorId(99)).thenReturn(null);

        assertThrows(ContratoInvalidoException.class, () -> service.editar(99, 1, 1, 1, 1, dataFinal));
        verify(contratoRepository, never()).atualizar(any());
    }

    @Test
    @Order(18)
    //Deve lançar ContratoInvalidoException ao editar um contrato com data final anterior à data atual.
    void testEditarDataInvalida() throws Exception {
        Contrato contrato = new Contrato(1, dataFinal, status, contratante, cliente, palhaco, ins, null);
        when(contratoRepository.buscarPorId(1)).thenReturn(contrato);
        when(contratanteRepository.buscarPorId(1)).thenReturn(contratante);
        when(clienteRepository.buscarPorId(1)).thenReturn(cliente);
        when(palhacoRepository.buscarPorId(1)).thenReturn(palhaco);
        when(instrumentoRepository.buscarPorId(1)).thenReturn(ins);

        assertThrows(ContratoInvalidoException.class, () -> service.editar(1, 1, 1, 1, 1, LocalDate.now().minusDays(1)));
        verify(contratoRepository, never()).atualizar(any());
    }

    @Test
    @Order(19)
    //Deve propagar SQLException ao editar um contrato quando ocorrer falha no banco de dados.
    void testEditarSqlException() throws Exception {
        when(contratoRepository.buscarPorId(1)).thenThrow(new SQLException("Erro ao editar"));

        assertThrows(SQLException.class, () -> service.editar(1, 1, 1, 1, 1, dataFinal));
    }

    @Test
    @Order(20)
    //Deve atualizar o status de um contrato pendente com sucesso.
    void testAtualizarStatusSucesso() throws Exception {
        Contrato contrato = new Contrato(1, dataFinal, status, contratante, cliente, palhaco, ins, null);
        when(contratoRepository.buscarPorId(1)).thenReturn(contrato);

        service.atualizarStatus(1, StatusContrato.REALIZADO);
        verify(contratoRepository, times(1)).atualizarStatus(1, StatusContrato.REALIZADO);
    }

    @Test
    @Order(21)
    //Deve lançar OperacaoNaoPermitidaException ao tentar atualizar status de um contrato inexistente.
    void testAtualizarStatusContratoNaoEncontrado() throws Exception {
        when(contratoRepository.buscarPorId(99)).thenReturn(null);

        assertThrows(OperacaoNaoPermitidaException.class, () -> service.atualizarStatus(99, StatusContrato.CANCELADO));
        verify(contratoRepository, never()).atualizarStatus(eq(99), any());
    }

    @Test
    @Order(22)
    //Deve lançar OperacaoNaoPermitidaException ao tentar atualizar status de um contrato que não está PENDENTE.
    void testAtualizarStatusContratoNaoPendente() throws Exception {
        Contrato contratoRealizado = new Contrato(2, dataFinal, StatusContrato.REALIZADO, contratante, cliente, palhaco, ins, null);
        when(contratoRepository.buscarPorId(2)).thenReturn(contratoRealizado);

        assertThrows(OperacaoNaoPermitidaException.class, () -> service.atualizarStatus(2, StatusContrato.CANCELADO));
        verify(contratoRepository, never()).atualizarStatus(anyInt(), any());
    }

    @Test
    @Order(23)
    //Deve propagar SQLException ao atualizar status quando ocorrer falha no banco de dados.
    void testAtualizarStatusSqlException() throws Exception {
        when(contratoRepository.buscarPorId(3)).thenThrow(new SQLException("Falha no banco"));

        assertThrows(SQLException.class, () -> service.atualizarStatus(3, StatusContrato.REALIZADO));
    }

    @Test
    @Order(24)
    //Deve remover um contrato por id com sucesso.
    void testRemover() throws Exception {
        service.remover(1);

        verify(contratoRepository, times(1)).remover(1);
    }

    @Test
    @Order(25)
    //Deve propagar SQLException ao remover um contrato quando ocorrer falha no banco de dados.
    void testRemoverSqlException() throws Exception {
        doThrow(new SQLException("Erro ao remover")).when(contratoRepository).remover(2);

        assertThrows(SQLException.class, () -> service.remover(2));
    }
}