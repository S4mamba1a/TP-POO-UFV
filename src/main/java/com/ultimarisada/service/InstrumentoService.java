package main.java.com.ultimarisada.service;
import java.sql.SQLException;
import java.util.List;

import main.java.com.ultimarisada.exceptions.InstrumentoEmUsoException;
import main.java.com.ultimarisada.model.InstrumentoDeRisada;
import main.java.com.ultimarisada.repository.ContratoRepository;
import main.java.com.ultimarisada.repository.InstrumentoRepository;

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
        if (contratoRepository.existeContratoPendenteComInstrumento(id)) {
            throw new InstrumentoEmUsoException("Não é possível remover o instrumento, pois ele está vinculado a um contrato PENDENTE.");
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
