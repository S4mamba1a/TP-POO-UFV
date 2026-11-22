package com.ultimarisada.repository;

import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.ultimarisada.model.Avaliacao;
import com.ultimarisada.model.Cliente;
import com.ultimarisada.model.Contratante;
import com.ultimarisada.model.Contrato;
import com.ultimarisada.model.InstrumentoDeRisada;
import com.ultimarisada.model.Palhaco;
import com.ultimarisada.model.StatusContrato;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ContratoRepositoryTest {
    ContratoRepository contratoRepository = new ContratoRepository();
    ContratanteRepository contratanteRepository = new ContratanteRepository();
    ClienteRepository clienteRepository = new ClienteRepository();
    PalhacoRepository palhacoRepository = new PalhacoRepository();
    InstrumentoRepository instrumentoRepository = new InstrumentoRepository();
    AvaliacaoRepository avaliacaoRepository = new AvaliacaoRepository();

    LocalDate dataFinal;
    StatusContrato status;
    Contratante contratante;
    Cliente cliente;
    Palhaco palhaco;
    InstrumentoDeRisada ins;

    @BeforeEach 
    //Configura o banco de dados H2 para ser usado nos testes, uma alternativa mais simples ao MySQL, e inicializa as variáveis a serem usadas.
    void configurarBancoVariaveis() throws SQLException{
        System.setProperty("dbTeste", "jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1");
        try (Connection conexao = ConexaoMySQL.obterConexao();
             Statement stmt = conexao.createStatement()){
            stmt.execute("DROP ALL OBJECTS;");
            stmt.execute("RUNSCRIPT FROM 'src/main/resources/db/ultimarisada-testes.sql'");
        }

        // Inicialização das variáveis do teste:
        dataFinal = LocalDate.now();
        status = StatusContrato.PENDENTE;
        contratante = new Contratante("José", LocalDate.now(), "1234");
        contratanteRepository.salvar(contratante);
        cliente = new Cliente("Joaquim", LocalDate.now(), "Homem alto de pele clara", "Rua Florestal, número 123", "Mercado", contratante);
        clienteRepository.salvar(cliente);
        palhaco = new Palhaco("Penny", LocalDate.now(), "Esquentadinho", 1890);
        palhacoRepository.salvar(palhaco);
        ins = new InstrumentoDeRisada(300, "Espada de Balão", "Corta até vento!");
        instrumentoRepository.salvar(ins, palhaco.getId());
    }

    @Test
    @Order(1)
    //Deve salvar e buscar um contrato por id com sucesso no banco de dados.
    void testSalvarBuscarPorId() throws SQLException {
        Contrato contrato = new Contrato(dataFinal, status, contratante, cliente, palhaco, ins);
        contratoRepository.salvar(contrato);
        Contrato contratoBuscado = contratoRepository.buscarPorId(contrato.getId());
        assertEquals(contrato.getId(), contratoBuscado.getId());
    }

    @Test
    @Order(2)
    //Deve retornar null ao buscar um contrato inexistente.
    void testBuscarPorIdNull() throws SQLException {
        Contrato contratoBuscado = contratoRepository.buscarPorId((int)Math.ceil(Math.random() * 100));
        assertNull(contratoBuscado);
    }

    @Test
    @Order(3)
    //Deve atualizar os dados de um contrato, com execeção do status e da avaliação.
    void testAtualizar() throws SQLException{
        Contrato contrato = new Contrato(dataFinal, status, contratante, cliente, palhaco, ins);
        contratoRepository.salvar(contrato);
        Contrato contratoAntigo = new Contrato(dataFinal, status, contratante, cliente, palhaco, ins);
        contratoAntigo.setId(contrato.getId());

        Cliente cliente2 = new Cliente("Samuel", dataFinal, "Homem alto de cabelo cacheado", "Rua Pipipipopopo, número 67", "Marte", contratante);
        clienteRepository.salvar(cliente2);
        Contratante contratante2 = new Contratante("Almiro", LocalDate.now(), "1234");
        contratanteRepository.salvar(contratante2);
        Palhaco palhaco2 = new Palhaco("Robertinho", LocalDate.now(), "Explosivo", 9000);
        palhacoRepository.salvar(palhaco2);
        InstrumentoDeRisada ins2 = new InstrumentoDeRisada(700, "Buzina de Carro em Alta Velocidade", "Te mata de rir com tanto barulho!");
        instrumentoRepository.salvar(ins2, palhaco2.getId());

        contrato.setDataFinal(LocalDate.of(2026, 12, 1));
        contrato.setCliente(cliente2);
        contrato.setContratante(contratante2);
        contrato.setPalhaco(palhaco2);
        contrato.setInstrumento(ins2);

        contrato.setAvaliacao(new Avaliacao(5, "Serviço muito bom!"));
        contrato.setStatus(StatusContrato.CANCELADA);

        contratoRepository.atualizar(contrato);
        Contrato contratoBuscado = contratoRepository.buscarPorId(contrato.getId());

        assertNotEquals(contratoAntigo.getDataFinal().toString(), contratoBuscado.getDataFinal().toString());
        assertNotEquals(contratoAntigo.getCliente().getId(), contratoBuscado.getCliente().getId());
        assertNotEquals(contratoAntigo.getContratante().getId(), contratoBuscado.getContratante().getId());
        assertNotEquals(contratoAntigo.getPalhaco().getId(), contratoBuscado.getPalhaco().getId());
        assertNotEquals(contratoAntigo.getInstrumento().getId(), contratoBuscado.getInstrumento().getId());
        assertNull(contratoBuscado.getAvaliacao());
        assertEquals(contratoAntigo.getStatus(), contratoBuscado.getStatus());
    }

    @Test
    @Order(4)
    //Deve atualizar o status de um contrato.
    void testAtualizarStatus() throws SQLException{
        Contrato contrato = new Contrato(dataFinal, status, contratante, cliente, palhaco, ins);
        contratoRepository.salvar(contrato);
        contratoRepository.atualizarStatus(contrato.getId(), StatusContrato.REALIZADA);
        Contrato contratoBuscado = contratoRepository.buscarPorId(contrato.getId());
        assertEquals(StatusContrato.REALIZADA, contratoBuscado.getStatus());
    }

    @Test
    @Order(5)
    //Deve vincular uma avaliação à um contrato.
    void testVincularAvaliacao() throws SQLException{
        Contrato contrato = new Contrato(dataFinal, status, contratante, cliente, palhaco, ins);
        contratoRepository.salvar(contrato);
        Avaliacao avaliacao = new Avaliacao(3, "Serviço poderia ter sido mais limpo.");
        avaliacaoRepository.salvar(avaliacao);
        contratoRepository.vincularAvaliacao(contrato.getId(), avaliacao.getId());
        Contrato contratoBuscado = contratoRepository.buscarPorId(contrato.getId());
        assertNotNull(contratoBuscado.getAvaliacao());
        assertEquals(avaliacao.getId(), contratoBuscado.getAvaliacao().getId());
    }

    @Test
    @Order(6)
    //Deve desvincular uma avaliação de um contrato.
    void testDesvincularAvaliacao() throws SQLException{
        Contrato contrato = new Contrato(dataFinal, status, contratante, cliente, palhaco, ins);
        contratoRepository.salvar(contrato);
        Avaliacao avaliacao = new Avaliacao(3, "Serviço poderia ter sido mais limpo.");
        avaliacaoRepository.salvar(avaliacao);
        contratoRepository.vincularAvaliacao(contrato.getId(), avaliacao.getId());
        contratoRepository.desvincularAvaliacao(contrato.getId());
        Contrato contratoBuscado = contratoRepository.buscarPorId(contrato.getId());
        assertNull(contratoBuscado.getAvaliacao());
    }

    @Test
    @Order(7)
    //Deve retornar true para a existencia de um contrato com determinado instrumento.
    void testExisteContratoComInstrumentoTrue() throws SQLException{
        Contrato contrato = new Contrato(dataFinal, status, contratante, cliente, palhaco, ins);
        contratoRepository.salvar(contrato);
        assertTrue(contratoRepository.existeContratoComInstrumento(ins.getId()));
    }

    @Test
    @Order(8)
    //Deve retornar false para a existencia de um contrato com determinado instrumento.
    void testExisteContratoComInstrumentoFalse() throws SQLException{
        InstrumentoDeRisada ins2 = new InstrumentoDeRisada(700, "Buzina de Carro em Alta Velocidade", "Te mata de rir com tanto barulho!");
        instrumentoRepository.salvar(ins2, palhaco.getId());
        Contrato contrato = new Contrato(dataFinal, StatusContrato.PENDENTE, contratante, cliente, palhaco, ins2);
        contratoRepository.salvar(contrato);
        assertFalse(contratoRepository.existeContratoComInstrumento(ins.getId()));
    }

    @Test
    @Order(9)
    //Deve retornar true para a existencia de um contrato com determinado cliente.
    void testExisteContratoComClienteTrue() throws SQLException{
        Contrato contrato = new Contrato(dataFinal, status, contratante, cliente, palhaco, ins);
        contratoRepository.salvar(contrato);
        assertTrue(contratoRepository.existeContratoComCliente(cliente.getId()));
    }

    @Test
    @Order(10)
    //Deve retornar false para a existencia de um contrato com determinado cliente.
    void testExisteContratoComClienteFalse() throws SQLException
    {   
        Cliente cliente2 = new Cliente("Samuel", dataFinal, "Homem alto de cabelo cacheado", "Rua Pipipipopopo, número 67", "Marte", contratante);
        clienteRepository.salvar(cliente2);
        Contrato contrato = new Contrato(dataFinal, status, contratante, cliente2, palhaco, ins);
        contratoRepository.salvar(contrato);
        assertFalse(contratoRepository.existeContratoComCliente(cliente.getId()));
    }

    @Test
    @Order(11)
    //Deve retornar true para a existencia de um contrato com determinado contratante.
    void testExisteContratoComContratanteTrue() throws SQLException{
        Contrato contrato = new Contrato(dataFinal, status, contratante, cliente, palhaco, ins);
        contratoRepository.salvar(contrato);
        assertTrue(contratoRepository.existeContratoComContratante(contratante.getId()));
    }

    @Test
    @Order(12)
    //Deve retornar false para a existencia de um contrato com determinado contratante.
    void testExisteContratoComContratanteFalse() throws SQLException{
        Contratante contratante2 = new Contratante("Almiro", LocalDate.now(), "1234");
        contratanteRepository.salvar(contratante2);
        Contrato contrato = new Contrato(dataFinal, status, contratante2, cliente, palhaco, ins);
        contratoRepository.salvar(contrato);
        assertFalse(contratoRepository.existeContratoComContratante(contratante.getId()));
    }

    @Test
    @Order(13)
    //Deve retornar true para a existencia de um contrato com determinado palhaço.
    void testExisteContratoComPalhacoTrue() throws SQLException{
        Contrato contrato = new Contrato(dataFinal, status, contratante, cliente, palhaco, ins);
        contratoRepository.salvar(contrato);
        assertTrue(contratoRepository.existeContratoComPalhaco(palhaco.getId()));
    }

    @Test
    @Order(14)
    //Deve retornar false para a existencia de um contrato com determinado palhaço.
    void testExisteContratoComPalhacoFalse() throws SQLException{
        Palhaco palhaco2 = new Palhaco("Robertinho", LocalDate.now(), "Explosivo", 9000);
        palhacoRepository.salvar(palhaco2);
        Contrato contrato = new Contrato(dataFinal, status, contratante, cliente, palhaco2, ins);
        contratoRepository.salvar(contrato);
        assertFalse(contratoRepository.existeContratoComPalhaco(palhaco.getId()));
    }

    @Test
    @Order(15)
    //Deve listar corretamente todos os contratos de um contratante.
    void testListarPorContratante() throws SQLException{
        Contrato contrato = new Contrato(dataFinal, status, contratante, cliente, palhaco, ins);
        contratoRepository.salvar(contrato);
        assertEquals(1, contratoRepository.listarPorContratante(contratante.getId()).size());
        assertTrue(contratoRepository.listarPorContratante(100).isEmpty());
    }

    @Test
    @Order(16)
    //Deve listar corretamente todos os contratos que possuem avaliação e que determinado palhaço participa.
    void testListarPorPalhacoComAvaliacao() throws SQLException{
        Contrato contrato = new Contrato(dataFinal, status, contratante, cliente, palhaco, ins);
        contratoRepository.salvar(contrato);
        assertEquals(0, contratoRepository.listarPorPalhacoComAvaliacao(palhaco.getId()).size());
        Avaliacao avaliacao = new Avaliacao(3, "Serviço poderia ter sido mais limpo.");
        avaliacaoRepository.salvar(avaliacao);
        contratoRepository.vincularAvaliacao(contrato.getId(), avaliacao.getId());
        assertEquals(1, contratoRepository.listarPorPalhacoComAvaliacao(palhaco.getId()).size());
    }

    @Test
    @Order(17)
    //Deve listar corretamente todos os contratos.
    void testListarTodos() throws SQLException{
        Contrato contrato = new Contrato(dataFinal, status, contratante, cliente, palhaco, ins);
        contratoRepository.salvar(contrato);
        assertEquals(1, contratoRepository.listarTodos().size());
    }

    @Test
    @Order(18)
    //Deve remover corretamente um contrato do banco de dados.
    void testRemover() throws SQLException{
        Contrato contrato = new Contrato(dataFinal, status, contratante, cliente, palhaco, ins);
        contratoRepository.salvar(contrato);
        contratoRepository.remover(contrato.getId());
        Contrato contratoBuscado = contratoRepository.buscarPorId(contrato.getId());
        assertNull(contratoBuscado);
    }
}
