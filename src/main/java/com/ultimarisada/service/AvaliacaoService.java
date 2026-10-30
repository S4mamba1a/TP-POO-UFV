package com.ultimarisada.service;

import com.ultimarisada.model.Contrato;
import com.ultimarisada.repository.AvaliacaoRepository;
import com.ultimarisada.repository.ContratoRepository;
import com.ultimarisada.repository.PalhacoRepository;
import com.ultimarisada.exceptions.AcessoNegadoException;
import com.ultimarisada.exceptions.OperacaoNaoPermitidaException;
import com.ultimarisada.exceptions.AvaliacaoDuplicadaException;
import com.ultimarisada.exceptions.ValorDeAvaliacaoInvalidoException;

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