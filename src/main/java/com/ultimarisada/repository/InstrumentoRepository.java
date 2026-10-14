package main.java.com.ultimarisada.repository;

import main.java.com.ultimarisada.model.InstrumentoDeRisada;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;



public class InstrumentoRepository {
    
    public void salvar(InstrumentoDeRisada i, int idPalhacoDono) throws SQLException{
        String sql = "INSERT INTO instrumento_de_risada" + "(nome, descricao, valor, id_palhaco)" + "VALUES (?, ?, ?, ?)";
        try (Connection conexao = ConexaoMySQL.obterConexao();
            PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
                stmt.setString(1, i.getNome());
                stmt.setString(2, i.getDescricao());
                stmt.setDouble(3, i.getValor());
                stmt.setInt(4, idPalhacoDono);
                stmt.executeUpdate();
                try (ResultSet chaves = stmt.getGeneratedKeys()){
                    if (chaves.next()){
                        i.setId(chaves.getInt(1));
                    }
                }
             }
    }

    public void atualizar(InstrumentoDeRisada i) throws SQLException{
        String sql = "UPDATE instrumento_de_risada SET nome = ?, descricao = ?, valor = ? WHERE id = ?";
        try (Connection conexao = ConexaoMySQL.obterConexao();
            PreparedStatement stmt = conexao.prepareStatement(sql)){
                stmt.setString(1, i.getNome());
                stmt.setString(2, i.getDescricao());
                stmt.setDouble(3, i.getValor());
                stmt.setInt(4, i.getId());
                stmt.executeUpdate();
             }
    }

    public void remover(int id) throws SQLException{
        String sql = "DELETE FROM instrumento_de_risada WHERE id = ?";
        try (Connection conexao = ConexaoMySQL.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)){
                stmt.setInt(1, id);
                stmt.executeUpdate();
             }
    }

    public InstrumentoDeRisada buscarPorId(int id) throws SQLException{
        String sql = "SELECT id, nome, descricao, valor " + "FROM instrumento_de_risada WHERE id = ?";
        try (Connection conexao = ConexaoMySQL.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)){
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()){
                    if (rs.next()){
                        return montarInstrumento(rs);
                    }
                }
             }
        return null;
    }

    public List<InstrumentoDeRisada> listarPorPalhaco(int idPalhaco) throws SQLException{
        List<InstrumentoDeRisada> instrumentos = new ArrayList<>();
        String sql = "SELECT id, nome, descricao, valor " + "FROM instrumento_de_risada WHERE id_palhaco = ?";
        try (Connection conexao = ConexaoMySQL.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)){
                stmt.setInt(1, idPalhaco);
                try (ResultSet rs = stmt.executeQuery()){
                    while (rs.next()){
                        instrumentos.add(montarInstrumento(rs));
                    }
                }
             }
        return instrumentos;
    }

    private InstrumentoDeRisada montarInstrumento(ResultSet rs) throws SQLException {
        return new InstrumentoDeRisada(rs.getInt("id"),rs.getDouble("valor"), rs.getString("nome"), rs.getString("descricao"));
    }
}
