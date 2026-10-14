package main.java.com.ultimarisada.repository;

import main.java.com.ultimarisada.model.Palhaco;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PalhacoRepository {

    //carregar os instrumentos de cada palhaço
    private InstrumentoRepository instrumentoRepository = new InstrumentoRepository();

    // insere um palhaço novo
    public void salvar(Palhaco p) throws SQLException {
        String sql = "INSERT INTO PALHACO (nome, dataNascimento, personalidade, nota, cache) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoMySQL.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, p.getNome());
            stmt.setDate(2, Date.valueOf(p.getDataNascimento()));
            stmt.setString(3, p.getPersonalidade());
            stmt.setDouble(4, 0); // nota inicial
            stmt.setDouble(5, p.getCache());
            stmt.executeUpdate();

            // pega o id gerado pelo banco e coloca no objeto
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1));
                }
            }
        }
    }

    // atualiza os dados do palhaço
    public void atualizar(Palhaco p) throws SQLException {
        String sql = "UPDATE PALHACO SET nome = ?, dataNascimento = ?, personalidade = ?, cache = ? "
                + "WHERE id = ?";

        try (Connection conn = ConexaoMySQL.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getNome());
            stmt.setDate(2, Date.valueOf(p.getDataNascimento()));
            stmt.setString(3, p.getPersonalidade());
            stmt.setDouble(4, p.getCache());
            stmt.setInt(5, p.getId());
            stmt.executeUpdate();
        }
    }

    public void remover(int id) throws SQLException {
        String sql = "DELETE FROM PALHACO WHERE id = ?";

        try (Connection conn = ConexaoMySQL.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Palhaco buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM PALHACO WHERE id = ?";

        try (Connection conn = ConexaoMySQL.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarPalhaco(rs);
                }
            }
        }
        return null;
    }

    public List<Palhaco> listarTodos() throws SQLException {
        String sql = "SELECT * FROM PALHACO";
        List<Palhaco> lista = new ArrayList<>();

        try (Connection conn = ConexaoMySQL.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(montarPalhaco(rs));
            }
        }
        return lista;
    }

    // busca pelo nome (pode voltar mais de um)
    public List<Palhaco> buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT * FROM PALHACO WHERE nome LIKE ?";
        List<Palhaco> lista = new ArrayList<>();

        try (Connection conn = ConexaoMySQL.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nome + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarPalhaco(rs));
                }
            }
        }
        return lista;
    }

    // filtra por personalidade
    public List<Palhaco> buscarPorPersonalidadeECacheMaximo(String personalidade, double cacheMaximo)
            throws SQLException {
        String sql = "SELECT * FROM PALHACO WHERE personalidade LIKE ? AND cache = ?";
        List<Palhaco> lista = new ArrayList<>();

        try (Connection conn = ConexaoMySQL.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + personalidade + "%");
            stmt.setDouble(2, cacheMaximo);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarPalhaco(rs));
                }
            }
        }
        return lista;
    }

    // do melhor para o pior
    public List<Palhaco> listarOrdenadoPorNota() throws SQLException {
        String sql = "SELECT * FROM PALHACO ORDER BY nota DESC";
        List<Palhaco> lista = new ArrayList<>();

        try (Connection conn = ConexaoMySQL.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(montarPalhaco(rs));
            }
        }
        return lista;
    }

    // atualiza so a nota
    public void atualizarNota(int idPalhaco, double novaNota) throws SQLException {
        String sql = "UPDATE PALHACO SET nota = ? WHERE id = ?";

        try (Connection conn = ConexaoMySQL.obterConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, novaNota);
            stmt.setInt(2, idPalhaco);
            stmt.executeUpdate();
        }
    }

    // transforma em um objeto Palhaco
    private Palhaco montarPalhaco(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");

        Palhaco p = new Palhaco(
                id,
                rs.getString("nome"),
                rs.getDate("dataNascimento").toLocalDate(),
                rs.getString("personalidade"),
                rs.getDouble("cache"),
                rs.getDouble("nota"));

        // carrega os instrumentos desse palhaço
        p.setInstrumentos(instrumentoRepository.listarPorPalhaco(id));

        return p;
    }
}
