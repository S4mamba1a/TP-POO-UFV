package main.java.com.ultimarisada.service;
import java.util.List;

import com.ultimarisada.model.InstrumentoDeRisada;
import com.ultimarisada.repository.InstrumentoRepository;
import com.ultimarisada.repository.ContratoRepository;
import com.ultimarisada.exceptions.InstrumentoEmUsoException;

public class InstrumentoService {
    private InstrumentoRepository instrumentoRepository;
    private ContratoRepository contratoRepository;

    public InstrumentoService(InstrumentoRepository instrumentoRepository, ContratoRepository contratoRepository) {
        this.instrumentoRepository = instrumentoRepository;
        this.contratoRepository = contratoRepository;
    }

    public void cadastrar(InstrumentoDeRisada i, int idPalhacoDono) {
        instrumentoRepository.salvar(i, idPalhacoDono);
    }

    public void editar(InstrumentoDeRisada i) {
        instrumentoRepository.atualizar(i);
    }

    public void remover(int id) throws InstrumentoEmUsoException {
        if (contratoRepository.existeContratoPendenteComInstrumento(id)) {
            throw new InstrumentoEmUsoException("Não é possível remover o instrumento, pois ele está vinculado a um contrato PENDENTE.");
        }
        instrumentoRepository.remover(id);
    }

    public InstrumentoDeRisada buscarPorId(int id) {
        return instrumentoRepository.buscarPorId(id);
    }

    public List<InstrumentoDeRisada> listarPorPalhaco(int idPalhaco) {
        return instrumentoRepository.listarPorPalhaco(idPalhaco);
    }
}
