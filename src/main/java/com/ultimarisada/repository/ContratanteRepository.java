package com.ultimarisada.repository;

import com.ultimarisada.model.Contratante;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ContratanteRepository {

    public void salvar(Contratante c) throws SQLException{
        String sql = "INSERT INTO CONTRATANTE (nome, data_nascimento, senha) VALUES (?, ?, ?)";
        try (Connection conexao = ConexaoMySQL.obterConexao(); PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            stmt.setString(1, c.getNome());
            stmt.setDate(2, Date.valueOf(c.getDataNascimento()));
            stmt.setString(3, c.getSenha());
            stmt.executeUpdate();
            try (ResultSet chaves = stmt.getGeneratedKeys()){
                if (chaves.next()){
                    c.setId(chaves.getInt(1));
                }
            }
        }
    }

    public void atualizar(Contratante c) throws SQLException{
        String sql = "UPDATE CONTRATANTE SET nome = ?, data_nascimento = ?, senha = ? WHERE id = ?";
        try (Connection conexao = ConexaoMySQL.obterConexao(); PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setString(1, c.getNome());
            stmt.setDate(2, Date.valueOf(c.getDataNascimento()));
            stmt.setString(3, c.getSenha());
            stmt.setInt(4, c.getId());
            stmt.executeUpdate();
        }
    }

    public void remover(int id) throws SQLException{
        String sql = "DELETE FROM CONTRATANTE WHERE id = ?";
        try (Connection conexao = ConexaoMySQL.obterConexao(); PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Contratante buscarPorId(int id) throws SQLException{
        String sql = "SELECT * FROM CONTRATANTE WHERE id = ?";
        try (Connection conexao = ConexaoMySQL.obterConexao(); PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    return montarContratante(rs);
                }
            }
        }
        return null;
    }

    // o nome do contratante é único, então volta no máximo um
    public Contratante buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT * FROM CONTRATANTE WHERE nome = ?";
        try (Connection conexao = ConexaoMySQL.obterConexao(); PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setString(1, nome);
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    return montarContratante(rs);
                }
            }
        }
        return null;
    }

    public List<Contratante> listarTodos() throws SQLException {
        List<Contratante> contratantes = new ArrayList<>();
        String sql = "SELECT * FROM CONTRATANTE";
        try (Connection conexao = ConexaoMySQL.obterConexao(); PreparedStatement stmt = conexao.prepareStatement(sql); ResultSet rs = stmt.executeQuery()){
            while (rs.next()){
                contratantes.add(montarContratante(rs));
            }
        }
        return contratantes;
    }

    private Contratante montarContratante(ResultSet rs) throws SQLException {
        return new Contratante(rs.getInt("id"),rs.getString("nome"), rs.getDate("data_nascimento").toLocalDate(), rs.getString("senha"));
    }
}