package main.java.com.ultimarisada.service;

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
            throws ClienteSemContratanteException, ClienteJaCadastradoException {
    }

    public void editar(Cliente c) throws ClienteJaCadastradoException {
    }

    public void remover(int id) throws ClienteEmUsoException {
    }

    public Cliente buscarPorId(int id) {
        return null;
    }

    public List<Cliente> listarTodos() {
        return null;
    }

    public List<Cliente> listarPorContratante(int idContratante) {
        return null;
    }
}
