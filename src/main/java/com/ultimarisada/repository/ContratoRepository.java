package com.ultimarisada.repository;

import com.ultimarisada.model.Avaliacao;
import com.ultimarisada.model.Cliente;
import com.ultimarisada.model.Contratante;
import com.ultimarisada.model.Contrato;
import com.ultimarisada.model.InstrumentoDeRisada;
import com.ultimarisada.model.Palhaco;
import com.ultimarisada.model.StatusContrato;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ContratoRepository {
    // usados para montar o contrato completo
    private ContratanteRepository contratanteRepository = new ContratanteRepository();
    private ClienteRepository clienteRepository = new ClienteRepository();
    private PalhacoRepository palhacoRepository = new PalhacoRepository();
    private InstrumentoRepository instrumentoRepository = new InstrumentoRepository();
    private AvaliacaoRepository avaliacaoRepository = new AvaliacaoRepository();

    // insere um contrato novo (sem avaliação)
    public void salvar(Contrato c) throws SQLException {
        String sql = "INSERT INTO CONTRATO (data_final, status, CONTRATANTE_id, CLIENTE_id, "
                + "PALHACO_id, INSTRUMENTO_DE_RISADA_id, AVALIACAO_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, NULL)";

        try (Connection conn = ConexaoMySQL.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, Date.valueOf(c.getDataFinal()));
            stmt.setString(2, c.getStatus().name());
            stmt.setInt(3, c.getContratante().getId());
            stmt.setInt(4, c.getCliente().getId());
            stmt.setInt(5, c.getPalhaco().getId());
            stmt.setInt(6, c.getInstrumento().getId());
            stmt.executeUpdate();

            // pega o id gerado pelo banco e coloca no objeto
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    c.setId(rs.getInt(1));
                }
            }
        }
    }

    // atualiza só os dados do contrato (não mexe no status nem na avaliação)
    public void atualizar(Contrato c) throws SQLException {
        String sql = "UPDATE CONTRATO SET data_final = ?, CONTRATANTE_id = ?, CLIENTE_id = ?, "
                + "PALHACO_id = ?, INSTRUMENTO_DE_RISADA_id = ? WHERE id = ?";

        try (Connection conn = ConexaoMySQL.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(c.getDataFinal()));
            stmt.setInt(2, c.getContratante().getId());
            stmt.setInt(3, c.getCliente().getId());
            stmt.setInt(4, c.getPalhaco().getId());
            stmt.setInt(5, c.getInstrumento().getId());
            stmt.setInt(6, c.getId());
            stmt.executeUpdate();
        }
    }

    // atualiza so o status
    public void atualizarStatus(int id, StatusContrato novo) throws SQLException {
        String sql = "UPDATE CONTRATO SET status = ? WHERE id = ?";

        try (Connection conn = ConexaoMySQL.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novo.name());
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public void vincularAvaliacao(int idContrato, int idAvaliacao) throws SQLException {
        String sql = "UPDATE CONTRATO SET AVALIACAO_id = ? WHERE id = ?";

        try (Connection conn = ConexaoMySQL.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idAvaliacao);
            stmt.setInt(2, idContrato);
            stmt.executeUpdate();
        }
    }

    public void desvincularAvaliacao(int idContrato) throws SQLException {
        String sql = "UPDATE CONTRATO SET AVALIACAO_id = NULL WHERE id = ?";

        try (Connection conn = ConexaoMySQL.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idContrato);
            stmt.executeUpdate();
        }
    }

    public void remover(int id) throws SQLException {
        int idAvaliacao = 0; // continua 0 se o contrato não tiver avaliação

        try (Connection conn = ConexaoMySQL.obterConexao()) {
            // guarda a avaliação antes de apagar, porque o contrato é quem aponta para ela
            try (PreparedStatement stmt = conn.prepareStatement("SELECT AVALIACAO_id FROM CONTRATO WHERE id = ?")) {
                stmt.setInt(1, id);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        idAvaliacao = rs.getInt("AVALIACAO_id");
                    }
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM CONTRATO WHERE id = ?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
        }

        // a avaliação do contrato removido também sai
        if (idAvaliacao != 0) {
            avaliacaoRepository.remover(idAvaliacao);
        }
    }

    public Contrato buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM CONTRATO WHERE id = ?";

        try (Connection conn = ConexaoMySQL.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarContrato(rs);
                }
            }
        }
        return null;
    }

    public List<Contrato> listarTodos() throws SQLException {
        String sql = "SELECT * FROM CONTRATO";
        List<Contrato> lista = new ArrayList<>();

        try (Connection conn = ConexaoMySQL.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(montarContrato(rs));
            }
        }
        return lista;
    }

    public List<Contrato> listarPorContratante(int idContratante) throws SQLException {
        String sql = "SELECT * FROM CONTRATO WHERE CONTRATANTE_id = ?";
        List<Contrato> lista = new ArrayList<>();

        try (Connection conn = ConexaoMySQL.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idContratante);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarContrato(rs));
                }
            }
        }
        return lista;
    }

    // só os contratos já avaliados, usado para recalcular a nota do palhaço
    public List<Contrato> listarPorPalhacoComAvaliacao(int idPalhaco) throws SQLException {
        String sql = "SELECT * FROM CONTRATO WHERE PALHACO_id = ? AND AVALIACAO_id IS NOT NULL";
        List<Contrato> lista = new ArrayList<>();

        try (Connection conn = ConexaoMySQL.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPalhaco);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarContrato(rs));
                }
            }
        }
        return lista;
    }

    public boolean existeContratoComContratante(int idContratante) throws SQLException {
        return existeContrato("CONTRATANTE_id", idContratante);
    }

    public boolean existeContratoComCliente(int idCliente) throws SQLException {
        return existeContrato("CLIENTE_id", idCliente);
    }

    public boolean existeContratoComPalhaco(int idPalhaco) throws SQLException {
        return existeContrato("PALHACO_id", idPalhaco);
    }

    public boolean existeContratoComInstrumento(int idInstrumento) throws SQLException {
        return existeContrato("INSTRUMENTO_DE_RISADA_id", idInstrumento);
    }

    // confere se existe contrato na coluna informada (o nome da coluna vem só dos métodos acima)
    private boolean existeContrato(String coluna, int id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM CONTRATO WHERE " + coluna + " = ?";

        try (Connection conn = ConexaoMySQL.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // transforma em um objeto Contrato
    private Contrato montarContrato(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        Contratante contratante = contratanteRepository.buscarPorId(rs.getInt("CONTRATANTE_id"));
        Cliente cliente = clienteRepository.buscarPorId(rs.getInt("CLIENTE_id"));
        Palhaco palhaco = palhacoRepository.buscarPorId(rs.getInt("PALHACO_id"));
        InstrumentoDeRisada instrumento = instrumentoRepository.buscarPorId(rs.getInt("INSTRUMENTO_DE_RISADA_id"));

        // a avaliação pode não existir ainda
        Avaliacao avaliacao = null;
        int idAvaliacao = rs.getInt("AVALIACAO_id");
        if (!rs.wasNull()) {
            avaliacao = avaliacaoRepository.buscarPorId(idAvaliacao);
        }

        return new Contrato(
                id,
                rs.getDate("data_final").toLocalDate(),
                StatusContrato.valueOf(rs.getString("status")),
                contratante,
                cliente,
                palhaco,
                instrumento,
                avaliacao);
    }
}