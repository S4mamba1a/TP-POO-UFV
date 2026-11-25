package com.ultimarisada.service;

import java.sql.SQLException;
import java.util.List;

import com.ultimarisada.exceptions.ClienteEmUsoException;
import com.ultimarisada.exceptions.ClienteJaCadastradoException;
import com.ultimarisada.exceptions.ClienteSemContratanteException;
import com.ultimarisada.exceptions.MenorDeIdadeException;
import com.ultimarisada.model.Cliente;
import com.ultimarisada.repository.ClienteRepository;
import com.ultimarisada.repository.ContratoRepository;

public class ClienteService {
    private ClienteRepository clienteRepository = new ClienteRepository();
    private ContratoRepository contratoRepository = new ContratoRepository();

    public ClienteService() {
    }

    public ClienteService(ClienteRepository clienteRepository, ContratoRepository contratoRepository) {
        this.clienteRepository = clienteRepository;
        this.contratoRepository = contratoRepository;
    }

    public void cadastrar(Cliente c)
            throws ClienteSemContratanteException, ClienteJaCadastradoException, MenorDeIdadeException, SQLException {
        if (c.getContratante() == null) {
            throw new ClienteSemContratanteException("O cliente precisa estar vinculado a um contratante.");
        }
        if(c.calcularIdade() < 18){
            throw new MenorDeIdadeException("O cliente precisa ser maior de idade.");
        }
        if (clienteRepository.buscarPorNome(c.getNome()) != null) {
            throw new ClienteJaCadastradoException("Já existe um cliente com esse nome.");
        }
        clienteRepository.salvar(c);
    }

    public void editar(Cliente c) throws ClienteJaCadastradoException, MenorDeIdadeException, SQLException {
        Cliente existente = clienteRepository.buscarPorNome(c.getNome());
        if (existente != null && existente.getId() != c.getId()) {
            throw new ClienteJaCadastradoException("Já existe um cliente com esse nome.");
        }
        if(c.calcularIdade() < 18){
            throw new MenorDeIdadeException("O cliente precisa ser maior de idade.");
        }
        clienteRepository.atualizar(c);
    }

    public void remover(int id) throws ClienteEmUsoException, SQLException {
        if (contratoRepository.existeContratoComCliente(id)) {
            throw new ClienteEmUsoException("O cliente está presente em algum contrato e não pode ser removido.");
        }
        clienteRepository.remover(id);
    }

    public Cliente buscarPorId(int id) throws SQLException{
        return clienteRepository.buscarPorId(id);
    }

    public List<Cliente> listarTodos() throws SQLException{
        return clienteRepository.listarTodos();
    }

    public List<Cliente> listarPorContratante(int idContratante) throws SQLException{
        return clienteRepository.listarPorContratante(idContratante);
    }
}
