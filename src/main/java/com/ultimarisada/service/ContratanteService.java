package main.java.com.ultimarisada.service;

import main.java.com.ultimarisada.model.Contratante;
import main.java.com.ultimarisada.repository.ContratanteRepository;
import main.java.com.ultimarisada.repository.ContratoRepository;
import main.java.com.ultimarisada.exceptions.ContratanteJaCadastradoException;
import main.java.com.ultimarisada.exceptions.CredenciaisInvalidasException;
import main.java.com.ultimarisada.exceptions.ContratanteEmUsoException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ContratanteService {
    private ContratanteRepository contratanteRepository;
    private ContratoRepository contratoRepository;

    public Contratante cadastrarProprio(String nome, LocalDate dataNascimento, String senha) throws ContratanteJaCadastradoException, SQLException {
        return null;
    }

    public Contratante autenticar(String nome, String senha) throws CredenciaisInvalidasException, SQLException {
        return null;
    }

    public void cadastrar(Contratante c) throws ContratanteJaCadastradoException, SQLException {
    }

    public void editar(Contratante c) throws ContratanteJaCadastradoException, SQLException {
    }

    public void remover(int id) throws ContratanteEmUsoException, SQLException {
    }

    public Contratante buscarPorId(int id) throws SQLException {
        return null;
    }

    public List<Contratante> listar() throws SQLException {
        return null;
    }
}