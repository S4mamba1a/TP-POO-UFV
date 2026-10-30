package main.java.com.ultimarisada.service;

import main.java.com.ultimarisada.model.Contrato;
import main.java.com.ultimarisada.repository.AvaliacaoRepository;
import main.java.com.ultimarisada.repository.ContratoRepository;
import main.java.com.ultimarisada.repository.PalhacoRepository;
import main.java.com.ultimarisada.exceptions.AcessoNegadoException;
import main.java.com.ultimarisada.exceptions.OperacaoNaoPermitidaException;
import main.java.com.ultimarisada.exceptions.AvaliacaoDuplicadaException;
import main.java.com.ultimarisada.exceptions.ValorDeAvaliacaoInvalidoException;

import java.sql.SQLException;
import java.util.List;

public class AvaliacaoService {
    private ContratoRepository contratoRepository;
    private AvaliacaoRepository avaliacaoRepository;
    private PalhacoRepository palhacoRepository;

    public void avaliar(int idContrato, int idContratanteLogado, int valor, String descricao) throws AcessoNegadoException, OperacaoNaoPermitidaException, AvaliacaoDuplicadaException, ValorDeAvaliacaoInvalidoException, SQLException {
    }

    public void editar(int idContrato, int valor, String descricao) throws OperacaoNaoPermitidaException, ValorDeAvaliacaoInvalidoException, SQLException {
    }

    public void remover(int idContrato) throws SQLException{
    }

    public double calcularNotaMedia(List<Contrato> contratosAvaliados) {
        return 0.0;
    }
}