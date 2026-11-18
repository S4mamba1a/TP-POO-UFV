package com.ultimarisada.service;

import com.ultimarisada.model.Cliente;
import com.ultimarisada.model.Contratante;
import com.ultimarisada.model.Contrato;
import com.ultimarisada.model.InstrumentoDeRisada;
import com.ultimarisada.model.Palhaco;
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
    private ContratoRepository contratoRepository = new ContratoRepository();
    private ContratanteRepository contratanteRepository = new ContratanteRepository();
    private ClienteRepository clienteRepository = new ClienteRepository();
    private PalhacoRepository palhacoRepository = new PalhacoRepository();
    private InstrumentoRepository instrumentoRepository = new InstrumentoRepository();

    public Contrato solicitar(int idContratante, int idCliente, int idPalhaco,
            int idInstrumento, LocalDate dataFinal) throws ContratoInvalidoException, SQLException {
        // o contrato nasce PENDENTE
        Contrato contrato = montarEValidar(idContratante, idCliente, idPalhaco, idInstrumento, dataFinal);
        contratoRepository.salvar(contrato);
        return contrato;
    }

    public void editar(int idContrato, int idContratante, int idCliente, int idPalhaco,
            int idInstrumento, LocalDate dataFinal) throws ContratoInvalidoException, SQLException {
        Contrato contrato = contratoRepository.buscarPorId(idContrato);
        if (contrato == null) {
            throw new ContratoInvalidoException("Contrato não encontrado.");
        }

        Contrato novo = montarEValidar(idContratante, idCliente, idPalhaco, idInstrumento, dataFinal);

        // muda só os dados, o status e a avaliação continuam os mesmos
        contrato.setContratante(novo.getContratante());
        contrato.setCliente(novo.getCliente());
        contrato.setPalhaco(novo.getPalhaco());
        contrato.setInstrumento(novo.getInstrumento());
        contrato.setDataFinal(novo.getDataFinal());

        contratoRepository.atualizar(contrato);
    }

    public double calcularValorTotal(Contrato c) {
        return c.getPalhaco().getCache() + c.getInstrumento().getValor();
    }

    public void atualizarStatus(int idContrato, StatusContrato novo)
            throws OperacaoNaoPermitidaException, SQLException {
        Contrato contrato = contratoRepository.buscarPorId(idContrato);
        if (contrato == null) {
            throw new OperacaoNaoPermitidaException("Contrato não encontrado.");
        }

        // só dá para mudar o status a partir de PENDENTE
        if (contrato.getStatus() != StatusContrato.PENDENTE) {
            throw new OperacaoNaoPermitidaException("O status só pode ser alterado em contratos PENDENTES.");
        }
        contratoRepository.atualizarStatus(idContrato, novo);
    }

    public void remover(int id) throws SQLException {
        contratoRepository.remover(id);
    }

    public Contrato buscarPorId(int id) throws SQLException {
        return contratoRepository.buscarPorId(id);
    }

    public List<Contrato> listarTodos() throws SQLException {
        return contratoRepository.listarTodos();
    }

    public List<Contrato> listarPorContratante(int idContratante) throws SQLException {
        return contratoRepository.listarPorContratante(idContratante);
    }

    // busca as entidades, valida e monta um contrato PENDENTE (ainda não salvo)
    private Contrato montarEValidar(int idContratante, int idCliente, int idPalhaco,
            int idInstrumento, LocalDate dataFinal) throws ContratoInvalidoException, SQLException {
        Contratante contratante = contratanteRepository.buscarPorId(idContratante);
        Cliente cliente = clienteRepository.buscarPorId(idCliente);
        Palhaco palhaco = palhacoRepository.buscarPorId(idPalhaco);
        InstrumentoDeRisada instrumento = instrumentoRepository.buscarPorId(idInstrumento);

        if (contratante == null || cliente == null || palhaco == null
                || instrumento == null || dataFinal == null) {
            throw new ContratoInvalidoException(
                    "Contratante, cliente, palhaço, instrumento e data final são obrigatórios.");
        }

        if (cliente.getContratante().getId() != idContratante) {
            throw new ContratoInvalidoException("O cliente escolhido não pertence a este contratante.");
        }

        if (dataFinal.isBefore(LocalDate.now())) {
            throw new ContratoInvalidoException("A data final não pode ser anterior à data de hoje.");
        }

        return new Contrato(dataFinal, StatusContrato.PENDENTE, contratante, cliente, palhaco, instrumento);
    }
}