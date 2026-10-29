package main.java.com.ultimarisada.service;

import main.java.com.ultimarisada.model.Contrato;
import main.java.com.ultimarisada.model.StatusContrato;
import main.java.com.ultimarisada.repository.ContratoRepository;
import main.java.com.ultimarisada.repository.ContratanteRepository;
import main.java.com.ultimarisada.repository.ClienteRepository;
import main.java.com.ultimarisada.repository.PalhacoRepository;
import main.java.com.ultimarisada.repository.InstrumentoRepository;
import main.java.com.ultimarisada.exceptions.ContratoInvalidoException;
import main.java.com.ultimarisada.exceptions.OperacaoNaoPermitidaException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ContratoService {
    private ContratoRepository contratoRepository;
    private ContratanteRepository contratanteRepository;
    private ClienteRepository clienteRepository;
    private PalhacoRepository palhacoRepository;
    private InstrumentoRepository instrumentoRepository;


    public Contrato solicitar(int idContratante, int idCliente, int idPalhaco,
            int idInstrumento, LocalDate dataFinal) throws ContratoInvalidoException, SQLException {
        return null;
    }

    public void editar(int idContrato, int idContratante, int idCliente, int idPalhaco,
            int idInstrumento, LocalDate dataFinal) throws ContratoInvalidoException, SQLException {
    }

    public double calcularValorTotal(Contrato c) {
        return 0.0;
    }

    public void atualizarStatus(int idContrato, StatusContrato novo)
            throws OperacaoNaoPermitidaException, SQLException {
    }

    public void remover(int id) throws SQLException {
    }

    public Contrato buscarPorId(int id) throws SQLException {
        return null;
    }

    public List<Contrato> listarTodos() throws SQLException {
        return null;
    }

    public List<Contrato> listarPorContratante(int idContratante) throws SQLException {
        return null;
    }
}