package main.java.com.ultimarisada.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import main.java.com.ultimarisada.model.Cliente;

public class ClienteRepository {

    public void salvar(Cliente c) throws SQLException {
    }

    public void atualizar(Cliente c) throws SQLException {
    }

    public void remover(int id) throws SQLException{
    }

    public Cliente buscarPorId(int id) throws SQLException{
        return null;
    }

    public Cliente buscarPorNome(String nome) throws SQLException{
        return null;
    }

    public List<Cliente> listarTodos() throws SQLException{
        return null;
    }

    public List<Cliente> listarPorContratante(int idContratante) throws SQLException{
        return null;
    }

    private Cliente montarCliente(ResultSet rs) throws SQLException{
        return null;
    }
}
