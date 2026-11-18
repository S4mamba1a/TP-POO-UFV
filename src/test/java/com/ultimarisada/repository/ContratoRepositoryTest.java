package com.ultimarisada.repository;

import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

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

    @BeforeEach 
    void configurarBanco() throws SQLException{ //configura o banco de dados H2 para ser usado nos testes, uma alternativa mais simples ao MySQL.
        System.setProperty("dbTeste", "jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1");
        try (Connection conexao = ConexaoMySQL.obterConexao();
             Statement stmt = conexao.createStatement()){
            stmt.execute("DROP ALL OBJECTS;");
            stmt.execute("RUNSCRIPT FROM 'src/main/resources/db/ultimarisada-testes.sql'");
        }
    }

    @Test
    @Order(1)
    void testSalvar() throws SQLException {
        // Inicialização das variáveis do teste:
        LocalDate dataFinal = LocalDate.now();
        StatusContrato status = StatusContrato.PENDENTE;
        Contratante contratante = new Contratante("José", LocalDate.now(), "1234");
        contratanteRepository.salvar(contratante);
        Cliente cliente = new Cliente("Joaquim", LocalDate.now(), "Homem alto de pele clara", "Rua Florestal, número 123", "Mercado", contratante);
        clienteRepository.salvar(cliente);
        Palhaco palhaco = new Palhaco("Penny", LocalDate.now(), "Esquentadinho", 1890);
        palhacoRepository.salvar(palhaco);
        InstrumentoDeRisada ins = new InstrumentoDeRisada(300, "Espada de Balão", "Corta até vento!");
        instrumentoRepository.salvar(ins, palhaco.getId());

        Contrato contrato = new Contrato(dataFinal, status, contratante, cliente, palhaco, ins);
        contratoRepository.salvar(contrato);
        Contrato contratoBuscado = contratoRepository.buscarPorId(contrato.getId());
        assertEquals(contrato, contratoBuscado);
    }

    @Test
    @Order(1)
    void testBuscarPorId() {

    }

    @Test
    @Order(1)
    void testAtualizar() {

    }

    @Test
    @Order(1)
    void testAtualizarStatus() {

    }

    @Test
    @Order(1)
    void testDesvincularAvaliacao() {

    }

    @Test
    @Order(1)
    void testExisteContratoPendenteComInstrumento() {

    }

    @Test
    @Order(1)
    void testExisteContratoPendenteDoCliente() {

    }

    @Test
    @Order(1)
    void testExisteContratoPendenteDoContratante() {

    }

    @Test
    @Order(1)
    void testExisteContratoPendenteDoPalhaco() {

    }

    @Test
    @Order(1)
    void testListarPorContratante() {

    }

    @Test
    @Order(1)
    void testListarPorPalhacoComAvaliacao() {

    }

    @Test
    @Order(1)
    void testListarTodos() {

    }

    @Test
    @Order(1)
    void testRemover() {

    }

    @Test
    @Order(1)
    void testVincularAvaliacao() {

    }
}
