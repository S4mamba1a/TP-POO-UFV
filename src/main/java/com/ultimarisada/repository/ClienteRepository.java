package com.ultimarisada.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ultimarisada.model.Cliente;
import com.ultimarisada.model.Contratante;

public class ClienteRepository {
    private final ContratanteRepository contratanteRepository = new ContratanteRepository();

    public void salvar(Cliente c) throws SQLException {
        String sql = "INSERT INTO CLIENTE "
                + "(nome, data_nascimento, descricao_fisica, endereco, local_de_trabalho, CONTRATANTE_id) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexao = ConexaoMySQL.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, c.getNome());
            stmt.setDate(2, Date.valueOf(c.getDataNascimento()));
            stmt.setString(3, c.getDescricaoFisica());
            stmt.setString(4, c.getEndereco());
            stmt.setString(5, c.getLocalDeTrabalho());
            stmt.setInt(6, c.getContratante().getId());
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    c.setId(chaves.getInt(1));
                }
            }
        }
    }

    public void atualizar(Cliente c) throws SQLException {
        String sql = "UPDATE CLIENTE SET nome = ?, data_nascimento = ?, descricao_fisica = ?, "
                + "endereco = ?, local_de_trabalho = ?, CONTRATANTE_id = ? WHERE id = ?";

        try (Connection conexao = ConexaoMySQL.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, c.getNome());
            stmt.setDate(2, Date.valueOf(c.getDataNascimento()));
            stmt.setString(3, c.getDescricaoFisica());
            stmt.setString(4, c.getEndereco());
            stmt.setString(5, c.getLocalDeTrabalho());
            stmt.setInt(6, c.getContratante().getId());
            stmt.setInt(7, c.getId());
            stmt.executeUpdate();
        }
    }

    public void remover(int id) throws SQLException {
        String sql = "DELETE FROM CLIENTE WHERE id = ?";
        try (Connection conexao = ConexaoMySQL.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM CLIENTE WHERE id = ?";
        try (Connection conexao = ConexaoMySQL.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarCliente(rs);
                }
            }
        }
        return null;
    }

    public Cliente buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT * FROM CLIENTE WHERE nome = ?";
        try (Connection conexao = ConexaoMySQL.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, nome);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarCliente(rs);
                }
            }
        }
        return null;
    }

    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM CLIENTE";
        try (Connection conexao = ConexaoMySQL.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                clientes.add(montarCliente(rs));
            }
        }
        return clientes;
    }

    public List<Cliente> listarPorContratante(int idContratante) throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM CLIENTE WHERE CONTRATANTE_id = ?";
        try (Connection conexao = ConexaoMySQL.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idContratante);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clientes.add(montarCliente(rs));
                }
            }
        }
        return clientes;
    }

    private Cliente montarCliente(ResultSet rs) throws SQLException {
        int contratanteId = rs.getInt("CONTRATANTE_id");
        Contratante contratante = contratanteRepository.buscarPorId(contratanteId);
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getDate("data_nascimento").toLocalDate(),
                rs.getString("descricao_fisica"),
                rs.getString("endereco"),
                rs.getString("local_de_trabalho"),
                contratante);
    }
}
