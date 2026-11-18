package com.ultimarisada.service;

import com.ultimarisada.model.Avaliacao;
import com.ultimarisada.model.Contrato;
import com.ultimarisada.model.StatusContrato;
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
    private ContratoRepository contratoRepository = new ContratoRepository();
    private AvaliacaoRepository avaliacaoRepository = new AvaliacaoRepository();
    private PalhacoRepository palhacoRepository = new PalhacoRepository();

    public void avaliar(int idContrato, int idContratanteLogado, int valor, String descricao) throws AcessoNegadoException, OperacaoNaoPermitidaException, AvaliacaoDuplicadaException, ValorDeAvaliacaoInvalidoException, SQLException {
        Contrato contrato = contratoRepository.buscarPorId(idContrato);
        if (contrato == null) {
            throw new OperacaoNaoPermitidaException("Contrato não encontrado.");
        }
        if (contrato.getContratante().getId() != idContratanteLogado) {
            throw new AcessoNegadoException("Esse contrato pertence a outro contratante.");
        }
        if (contrato.getStatus() == StatusContrato.PENDENTE) {
            throw new OperacaoNaoPermitidaException("Não é possível avaliar um contrato pendente.");
        }
        if (contrato.getAvaliacao() != null) {
            throw new AvaliacaoDuplicadaException("Esse contrato já foi avaliado.");
        }
        if (valor < 1 || valor > 5) {
            throw new ValorDeAvaliacaoInvalidoException("A nota deve ser de 1 a 5.");
        }

        Avaliacao avaliacao = new Avaliacao(valor, descricao);
        avaliacaoRepository.salvar(avaliacao);
        contratoRepository.vincularAvaliacao(idContrato, avaliacao.getId());

        atualizarNotaDoPalhaco(contrato.getPalhaco().getId());
    }

    public void editar(int idContrato, int valor, String descricao) throws OperacaoNaoPermitidaException, ValorDeAvaliacaoInvalidoException, SQLException {
        Contrato contrato = contratoRepository.buscarPorId(idContrato);
        if (contrato == null) {
            throw new OperacaoNaoPermitidaException("Contrato não encontrado.");
        }
        if (contrato.getAvaliacao() == null) {
            throw new OperacaoNaoPermitidaException("Esse contrato ainda não tem avaliação para editar.");
        }
        if (valor < 1 || valor > 5) {
            throw new ValorDeAvaliacaoInvalidoException("A nota deve ser de 1 a 5.");
        }

        Avaliacao avaliacao = contrato.getAvaliacao();
        avaliacao.setValor(valor);
        avaliacao.setDescricao(descricao);
        avaliacaoRepository.atualizar(avaliacao);

        atualizarNotaDoPalhaco(contrato.getPalhaco().getId());
    }

    public void remover(int idContrato) throws SQLException {
        Contrato contrato = contratoRepository.buscarPorId(idContrato);
        if (contrato == null || contrato.getAvaliacao() == null) {
            return;
        }
        int idAvaliacao = contrato.getAvaliacao().getId();

        // o contrato aponta pra avaliação (FK), então tem que desvincular antes de apagar
        contratoRepository.desvincularAvaliacao(idContrato);
        avaliacaoRepository.remover(idAvaliacao);

        atualizarNotaDoPalhaco(contrato.getPalhaco().getId());
    }

    public double calcularNotaMedia(List<Contrato> contratosAvaliados) {
        if (contratosAvaliados.isEmpty()) {
            return 0;
        }
        double soma = 0;
        for (Contrato contrato : contratosAvaliados) {
            soma += contrato.getAvaliacao().getValor();
        }
        return soma / contratosAvaliados.size();
    }

    // recalcula a média com as avaliações que sobraram e grava no palhaço
    private void atualizarNotaDoPalhaco(int idPalhaco) throws SQLException {
        List<Contrato> avaliados = contratoRepository.listarPorPalhacoComAvaliacao(idPalhaco);
        palhacoRepository.atualizarNota(idPalhaco, calcularNotaMedia(avaliados));
    }
}