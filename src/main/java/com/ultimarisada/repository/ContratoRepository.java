package com.ultimarisada.repository;

import com.ultimarisada.model.Contrato;
import com.ultimarisada.model.StatusContrato;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ContratoRepository {
    private ContratanteRepository contratanteRepository;
    private ClienteRepository clienteRepository;
    private PalhacoRepository palhacoRepository;
    private InstrumentoRepository instrumentoRepository;
    private AvaliacaoRepository avaliacaoRepository;

    public void salvar(Contrato c) throws SQLException {
    }

    public void atualizarStatus(int id, StatusContrato novo) throws SQLException {
    }

    public void vincularAvaliacao(int idContrato, int idAvaliacao) throws SQLException {
    }

    public void desvincularAvaliacao(int idContrato) throws SQLException {
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

    public List<Contrato> listarPorPalhacoComAvaliacao(int idPalhaco) throws SQLException {
        return null;
    }

    public boolean existeContratoPendenteDoContratante(int idContratante) throws SQLException {
        return true;
    }

    public boolean existeContratoPendenteDoCliente(int idCliente) throws SQLException {
        return true;
    }

    public boolean existeContratoPendenteDoPalhaco(int idPalhaco) throws SQLException {
        return true;
    }

    public boolean existeContratoPendenteComInstrumento(int idInstrumento) throws SQLException {
        return true;
    }

    private Contrato montarContrato(ResultSet rs) throws SQLException {
        return null;
    }
}