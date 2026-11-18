package com.ultimarisada.repository;

import com.ultimarisada.model.Avaliacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AvaliacaoRepository {

    public void salvar(Avaliacao a) throws SQLException {
        String sql = "INSERT INTO AVALIACAO (valor, descricao) VALUES (?, ?)";
        try (Connection conexao = ConexaoMySQL.obterConexao(); PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            stmt.setInt(1, a.getValor());
            stmt.setString(2, a.getDescricao());
            stmt.executeUpdate();
            try (ResultSet chaves = stmt.getGeneratedKeys()){
                if (chaves.next()){
                    a.setId(chaves.getInt(1));
                }
            }
        }
    }

    public void atualizar(Avaliacao a) throws SQLException {
        String sql = "UPDATE AVALIACAO SET valor = ?, descricao = ? WHERE id = ?";
        try (Connection conexao = ConexaoMySQL.obterConexao(); PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setInt(1, a.getValor());
            stmt.setString(2, a.getDescricao());
            stmt.setInt(3, a.getId());
            stmt.executeUpdate();
        }
    }

    public void remover(int id) throws SQLException {
        String sql = "DELETE FROM AVALIACAO WHERE id = ?";
        try (Connection conexao = ConexaoMySQL.obterConexao(); PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Avaliacao buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM AVALIACAO WHERE id = ?";
        try (Connection conexao = ConexaoMySQL.obterConexao(); PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    return montarAvaliacao(rs);
                }
            }
        }
        return null;
    }

    private Avaliacao montarAvaliacao(ResultSet rs) throws SQLException {
        return new Avaliacao(rs.getInt("id"), rs.getInt("valor"), rs.getString("descricao"));
    }
}