package com.ultimarisada.service;

import com.ultimarisada.model.Contrato;
import com.ultimarisada.model.StatusContrato;
import com.ultimarisada.repository.ContratoRepository;
import com.ultimarisada.repository.ContratanteRepository;
import com.ultimarisada.repository.ClienteRepository;
import com.ultimarisada.repository.PalhacoRepository;
import com.ultimarisada.repository.InstrumentoRepository;
import com.ultimarisada.exceptions.ContratoInvalidoException;
import com.ultimarisada.exceptions.OperacaoNaoPermitidaException;
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