package com.ultimarisada.service;
import java.sql.SQLException;
import java.util.List;

import com.ultimarisada.exceptions.InstrumentoEmUsoException;
import com.ultimarisada.model.InstrumentoDeRisada;
import com.ultimarisada.repository.ContratoRepository;
import com.ultimarisada.repository.InstrumentoRepository;

public class InstrumentoService {
    private InstrumentoRepository instrumentoRepository = new InstrumentoRepository();
    private ContratoRepository contratoRepository = new ContratoRepository();

    public void cadastrar(InstrumentoDeRisada i, int idPalhacoDono) throws SQLException {
        instrumentoRepository.salvar(i, idPalhacoDono);
    }

    public void editar(InstrumentoDeRisada i) throws SQLException {
        instrumentoRepository.atualizar(i);
    }

    public void remover(int id) throws InstrumentoEmUsoException, SQLException {
        if (contratoRepository.existeContratoComInstrumento(id)) {
            throw new InstrumentoEmUsoException("O intrumento de risada está em contratos e não pode ser removido.");
        }
        instrumentoRepository.remover(id);
    }

    public InstrumentoDeRisada buscarPorId(int id) throws SQLException {
        return instrumentoRepository.buscarPorId(id);
    }

    public List<InstrumentoDeRisada> listarPorPalhaco(int idPalhaco) throws SQLException {
        return instrumentoRepository.listarPorPalhaco(idPalhaco);
    }
}
