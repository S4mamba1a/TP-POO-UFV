package com.ultimarisada.service;

import com.ultimarisada.model.Contratante;
import com.ultimarisada.repository.ContratanteRepository;
import com.ultimarisada.repository.ContratoRepository;
import com.ultimarisada.exceptions.ContratanteJaCadastradoException;
import com.ultimarisada.exceptions.CredenciaisInvalidasException;
import com.ultimarisada.exceptions.ContratanteEmUsoException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ContratanteService {
    private ContratanteRepository contratanteRepository = new ContratanteRepository();
    private ContratoRepository contratoRepository = new ContratoRepository();

    public Contratante cadastrarProprio(String nome, LocalDate dataNascimento, String senha) throws ContratanteJaCadastradoException, SQLException {
        if (contratanteRepository.buscarPorNome(nome) != null) {
            throw new ContratanteJaCadastradoException("Já existe um contratante com esse nome.");
        }
        Contratante c = new Contratante(nome, dataNascimento, senha);
        contratanteRepository.salvar(c);
        return c;
    }

    public Contratante autenticar(String nome, String senha) throws CredenciaisInvalidasException, SQLException {
        Contratante c = contratanteRepository.buscarPorNome(nome);
        if (c == null || !c.getSenha().equals(senha)) {
            throw new CredenciaisInvalidasException("Nome ou senha incorretos.");
        }
        return c;
    }

    public void cadastrar(Contratante c) throws ContratanteJaCadastradoException, SQLException {
        if (contratanteRepository.buscarPorNome(c.getNome()) != null) {
            throw new ContratanteJaCadastradoException("Já existe um contratante com esse nome.");
        }
        contratanteRepository.salvar(c);
    }

    public void editar(Contratante c) throws ContratanteJaCadastradoException, SQLException {
        Contratante existente = contratanteRepository.buscarPorNome(c.getNome());
        // ignora o próprio registro, senão não dá pra editar sem mudar o nome
        if (existente != null && existente.getId() != c.getId()) {
            throw new ContratanteJaCadastradoException("Já existe um contratante com esse nome.");
        }
        contratanteRepository.atualizar(c);
    }

    public void remover(int id) throws ContratanteEmUsoException, SQLException {
        if (contratoRepository.existeContratoPendenteDoContratante(id)) {
            throw new ContratanteEmUsoException("O contratante possui contratos pendentes e não pode ser removido.");
        }
        contratanteRepository.remover(id);
    }

    public Contratante buscarPorId(int id) throws SQLException {
        return contratanteRepository.buscarPorId(id);
    }

    public List<Contratante> listar() throws SQLException {
        return contratanteRepository.listarTodos();
    }
}