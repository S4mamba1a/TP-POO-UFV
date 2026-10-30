package main.java.com.ultimarisada.service;

import java.sql.SQLException;
import java.util.List;

import main.java.com.ultimarisada.exceptions.ClienteEmUsoException;
import main.java.com.ultimarisada.exceptions.ClienteJaCadastradoException;
import main.java.com.ultimarisada.exceptions.ClienteSemContratanteException;
import main.java.com.ultimarisada.model.Cliente;
import main.java.com.ultimarisada.repository.ClienteRepository;
import main.java.com.ultimarisada.repository.ContratoRepository;

public class ClienteService {
    private ClienteRepository clienteRepository;
    private ContratoRepository contratoRepository;

    public void cadastrar(Cliente c)
            throws ClienteSemContratanteException, ClienteJaCadastradoException, SQLException {
    }

    public void editar(Cliente c) throws ClienteJaCadastradoException, SQLException {
    }

    public void remover(int id) throws ClienteEmUsoException, SQLException {
    }

    public Cliente buscarPorId(int id) throws SQLException{
        return null;
    }

    public List<Cliente> listarTodos() throws SQLException{
        return null;
    }

    public List<Cliente> listarPorContratante(int idContratante) throws SQLException{
        return null;
    }
}
