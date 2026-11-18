package com.ultimarisada.repository;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ContratoRepositoryTest {
    ContratoRepository contratoRepository;

    @BeforeEach 
    void configurarBanco() throws SQLException{ //configura o banco de dados H2 para ser usado nos testes, uma alternativa mais simples ao MySQL.
        System.setProperty("dbTeste", "jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1");
        try (Connection conexao = ConexaoMySQL.obterConexao();
             Statement stmt = conexao.createStatement()){
            stmt.execute("DROP ALL OBJECTS;");
            stmt.execute("RUNSCRIPT FROM 'src/main/resources/db/ultimarisada.sql'");
        }
    }

    @Test
    void testAtualizar() {

    }

    @Test
    void testAtualizarStatus() {

    }

    @Test
    void testBuscarPorId() {

    }

    @Test
    void testDesvincularAvaliacao() {

    }

    @Test
    void testExisteContratoPendenteComInstrumento() {

    }

    @Test
    void testExisteContratoPendenteDoCliente() {

    }

    @Test
    void testExisteContratoPendenteDoContratante() {

    }

    @Test
    void testExisteContratoPendenteDoPalhaco() {

    }

    @Test
    void testListarPorContratante() {

    }

    @Test
    void testListarPorPalhacoComAvaliacao() {

    }

    @Test
    void testListarTodos() {

    }

    @Test
    void testRemover() {

    }

    @Test
    void testSalvar() {

    }

    @Test
    void testVincularAvaliacao() {

    }
}
