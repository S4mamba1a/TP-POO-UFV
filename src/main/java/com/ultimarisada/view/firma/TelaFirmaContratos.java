package com.ultimarisada.view.firma;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ultimarisada.exceptions.ContratoInvalidoException;
import com.ultimarisada.exceptions.OperacaoNaoPermitidaException;
import com.ultimarisada.model.Cliente;
import com.ultimarisada.model.Contratante;
import com.ultimarisada.model.Contrato;
import com.ultimarisada.model.InstrumentoDeRisada;
import com.ultimarisada.model.Palhaco;
import com.ultimarisada.model.StatusContrato;
import com.ultimarisada.service.ClienteService;
import com.ultimarisada.service.ContratanteService;
import com.ultimarisada.service.ContratoService;
import com.ultimarisada.service.InstrumentoService;
import com.ultimarisada.service.PalhacoService;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

public class TelaFirmaContratos {
    private Stage stage;
    private ContratoService contratoService;
    private ContratanteService contratanteService;
    private ClienteService clienteService;
    private PalhacoService palhacoService;
    private InstrumentoService instrumentoService;
    private Contrato selecionado;

    @FXML private ComboBox<Contratante> comboContratante;
    @FXML private ComboBox<Cliente> comboCliente;
    @FXML private ComboBox<Palhaco> comboPalhaco;
    @FXML private ComboBox<InstrumentoDeRisada> comboInstrumento;
    @FXML private DatePicker campoDataFinal;
    @FXML private Label rotuloValorTotal;
    @FXML private ComboBox<StatusContrato> comboNovoStatus;

    @FXML private Button botaoAtualizarStatus;
    @FXML private Button botaoSalvar;
    @FXML private Button botaoRemover;
    @FXML private ComboBox<Contratante> comboFiltroContratante;
    @FXML private ComboBox<Palhaco> comboFiltroPalhaco;
    @FXML private ComboBox<StatusContrato> comboFiltroStatus;

    @FXML private TableView<Contrato> tabelaContratos;
    @FXML private TableColumn<Contrato, Integer> colunaId;
    @FXML private TableColumn<Contrato, String> colunaContratante;
    @FXML private TableColumn<Contrato, String> colunaCliente;
    @FXML private TableColumn<Contrato, String> colunaPalhaco;
    @FXML private TableColumn<Contrato, String> colunaInstrumento;
    @FXML private TableColumn<Contrato, LocalDate> colunaDataFinal;
    @FXML private TableColumn<Contrato, StatusContrato> colunaStatus;
    @FXML private TableColumn<Contrato, String> colunaValorTotal;
    

    public TelaFirmaContratos(Stage stage) {
        this.stage = stage;
        this.contratoService = new ContratoService();
        this.contratanteService = new ContratanteService();
        this.clienteService = new ClienteService();
        this.palhacoService = new PalhacoService();
        this.instrumentoService = new InstrumentoService();
        this.selecionado = null;
    }

    public void exibir() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/firma/TelaFirmaContratos.fxml"));
            loader.setController(this);
            Parent raiz = loader.load();
            stage.setScene(new Scene(raiz));
            stage.setTitle("Firma — Contratos");
            stage.sizeToScene();
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao carregar /view/firma/TelaFirmaContratos.fxml", e);
        }
    }

    @FXML private void initialize() {
        StringConverter<Contratante> conversorContratante = new StringConverter<>() {
            @Override public String toString(Contratante c) { return c == null ? "" : c.exibirResumo(); }
            @Override public Contratante fromString(String s) { return null; }
        };
        comboContratante.setConverter(conversorContratante);
        comboFiltroContratante.setConverter(conversorContratante);

        StringConverter<Palhaco> conversorPalhaco = new StringConverter<>() {
            @Override public String toString(Palhaco p) { return p == null ? "" : p.exibirResumo(); }
            @Override public Palhaco fromString(String s) { return null; }
        };
        comboPalhaco.setConverter(conversorPalhaco);
        comboFiltroPalhaco.setConverter(conversorPalhaco);

        comboCliente.setConverter(new StringConverter<>() {
            @Override public String toString(Cliente c) { return c == null ? "" : c.exibirResumo(); }
            @Override public Cliente fromString(String s) { return null; }
        });

        comboInstrumento.setConverter(new StringConverter<>() {
            @Override public String toString(InstrumentoDeRisada i) { return i == null ? "" : i.getNome(); }
            @Override public InstrumentoDeRisada fromString(String s) { return null; }
        });

        comboNovoStatus.getItems().setAll(StatusContrato.REALIZADO, StatusContrato.CANCELADO);
        comboFiltroStatus.getItems().setAll(StatusContrato.values());

        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaDataFinal.setCellValueFactory(new PropertyValueFactory<>("dataFinal"));
        colunaStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colunaContratante.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getContratante().getNome()));
        colunaCliente.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getCliente().getNome()));
        colunaPalhaco.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getPalhaco().getNome()));
        colunaInstrumento.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getInstrumento().getNome()));
        colunaValorTotal.setCellValueFactory(d -> {
            double total = contratoService.calcularValorTotal(d.getValue());
            return new SimpleStringProperty(String.format("R$ %.2f", total));
        });

        tabelaContratos.getSelectionModel().selectedItemProperty()
            .addListener((obs, antigo, novo) -> onLinhaSelecionada(novo));

        carregarContratantes();
        carregarPalhacos();
        carregarLista();
        onNovoClicado();
    }

    //aqui vai o carregarContratante
    private void carregarContratantes() {
        try {
            List<Contratante> lista = contratanteService.listar();
            comboContratante.getItems().setAll(lista);
            comboFiltroContratante.getItems().setAll(lista);
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }

    //carregar palhaco
    private void carregarPalhacos() {
        try {
            List<Palhaco> lista = palhacoService.listarTodos();
            comboPalhaco.getItems().setAll(lista);
            comboFiltroPalhaco.getItems().setAll(lista);
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }

    //carregar a lista
    private void carregarLista() {
        try {
            List<Contrato> lista;
            if (comboFiltroContratante.getValue() == null) {
                lista = contratoService.listarTodos();
            } else {
                lista = contratoService.listarPorContratante(comboFiltroContratante.getValue().getId());
            }

            Palhaco filtroPalhaco = comboFiltroPalhaco.getValue();
            StatusContrato filtroStatus = comboFiltroStatus.getValue();

            List<Contrato> filtrados = new ArrayList<>();
            for (Contrato c : lista) {
                boolean passaPalhaco = filtroPalhaco == null
                    || c.getPalhaco().getId() == filtroPalhaco.getId();
                boolean passaStatus = filtroStatus == null
                    || c.getStatus() == filtroStatus;

                if (passaPalhaco && passaStatus) {
                    filtrados.add(c);
                }
            }

            tabelaContratos.getItems().setAll(filtrados);
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }

    //chama a lista quando alterado o filtro
    @FXML private void onFiltroAlterado() {
        carregarLista();
    }

    //filtro volta a ser null
    @FXML private void onLimparFiltrosClicado() {
        comboFiltroContratante.setValue(null);
        comboFiltroPalhaco.setValue(null);
        comboFiltroStatus.setValue(null);
    }

    //se contratante null esvazia cliente, senao chama listarPorContratante para preencher cliente
    @FXML private void onContratanteSelecionado() {
        Contratante c = comboContratante.getValue();
        if (c == null) {
            comboCliente.getItems().clear();
            return;
        }
        try {
            comboCliente.getItems().setAll(clienteService.listarPorContratante(c.getId()));
            comboCliente.setValue(null);
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }

    //mesmo para palhaco, exceto que agora a gente trata o valor total
    @FXML private void onPalhacoSelecionado() {
        Palhaco p = comboPalhaco.getValue();
        if (p == null) {
            comboInstrumento.getItems().clear();
            return;
        }
        try {
            comboInstrumento.getItems().setAll(instrumentoService.listarPorPalhaco(p.getId()));
            comboInstrumento.setValue(null);
            atualizarValorTotal();
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }

    //utiliza contratoService para calcular o valor total do palhaco+instrumento de risada
    private void atualizarValorTotal() {
        Palhaco p = comboPalhaco.getValue();
        InstrumentoDeRisada i = comboInstrumento.getValue();
        if (p == null || i == null) {
            rotuloValorTotal.setText("Valor total: —");
            return;
        }
        Contrato temporario = new Contrato(); //// o responsavel do backend tem que adicionar um construtor vazio pra eu usar, nn vou mecher la pra nn da conflito de merge porque to mechendo faz tempo nesse codigo sem dar fetch
        temporario.setPalhaco(p);
        temporario.setInstrumento(i);
        double total = contratoService.calcularValorTotal(temporario);
        rotuloValorTotal.setText(String.format("Valor total: R$ %.2f", total));
    }

    @FXML private void onInstrumentoSelecionado() {
        atualizarValorTotal();
    }

    
    private void onLinhaSelecionada(Contrato novo) {
        if (novo == null) return;
        selecionado = novo;
        preencherFormulario(novo);
        botaoRemover.setDisable(false);
        boolean pendente = novo.getStatus() == StatusContrato.PENDENTE;
        comboNovoStatus.setDisable(!pendente);
        botaoAtualizarStatus.setDisable(!pendente);
    }

    //desabilita remover e novo status e limpa o form
    @FXML private void onNovoClicado() {
        selecionado = null;
        limparFormulario();
        tabelaContratos.getSelectionModel().clearSelection();
        botaoSalvar.setText("Salvar");
        botaoRemover.setDisable(true);
        comboNovoStatus.setDisable(true);
        botaoAtualizarStatus.setDisable(true);
    }


    @FXML private void onSalvarClicado() {
        Contrato c = lerFormulario();
        if (c == null) return;

        try {
            if (selecionado == null) {
                contratoService.solicitar(
                    c.getContratante().getId(),
                    c.getCliente().getId(),
                    c.getPalhaco().getId(),
                    c.getInstrumento().getId(),
                    c.getDataFinal());
                exibirSucesso("Contrato criado com sucesso. Valor total: R$ "
                    + String.format("%.2f", contratoService.calcularValorTotal(c)));
            } else {
                contratoService.editar(
                    selecionado.getId(),
                    c.getContratante().getId(),
                    c.getCliente().getId(),
                    c.getPalhaco().getId(),
                    c.getInstrumento().getId(),
                    c.getDataFinal());
                exibirSucesso("Contrato atualizado com sucesso. Valor total: R$ "
                    + String.format("%.2f", contratoService.calcularValorTotal(c)));
            }
            carregarLista();
            onNovoClicado();
        } catch (ContratoInvalidoException e) {
            exibirErro(e.getMessage());
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }

    @FXML private void onAtualizarStatusClicado() {
        if (selecionado == null || comboNovoStatus.getValue() == null) {
            exibirErro("Selecione um contrato na tabela e o novo status.");
            return;
        }
        StatusContrato novo = comboNovoStatus.getValue();
        if (!confirmar("Marcar o contrato " + selecionado.getId() + " como " + novo + "?")) return;
        try {
            contratoService.atualizarStatus(selecionado.getId(), novo);
            exibirSucesso("Status atualizado com sucesso.");
            carregarLista();
            onNovoClicado();
        } catch (OperacaoNaoPermitidaException e) {
            exibirErro(e.getMessage());
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }

    @FXML private void onRemoverClicado() {
        if (selecionado == null) {
            exibirErro("Selecione um contrato na tabela para remover.");
            return;
        }
        if (!confirmar("Remover o contrato " + selecionado.getId() + "?")) return;
        try {
            contratoService.remover(selecionado.getId());
            exibirSucesso("Contrato removido com sucesso.");
            carregarLista();
            onNovoClicado();
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }

    @FXML private void onVoltarClicado() {
        new TelaFirmaMenu(stage).exibir();
    }

    private Contrato lerFormulario() {
        if (comboContratante.getValue() == null) { exibirErro("Selecione o contratante."); return null; }
        if (comboCliente.getValue() == null) { exibirErro("Selecione o cliente."); return null; }
        if (comboPalhaco.getValue() == null) { exibirErro("Selecione o palhaço."); return null; }
        if (comboInstrumento.getValue() == null) { exibirErro("Selecione o instrumento."); return null; }
        if (campoDataFinal.getValue() == null) { exibirErro("O campo data final está inválido."); return null; }

        Contrato c = new Contrato(); // o responsavel do backend tem que adicionar um construtor vazio pra eu usar, nn vou mecher la pra nn da conflito de merge porque to mechendo faz tempo nesse codigo sem dar fetch
        c.setContratante(comboContratante.getValue());
        c.setCliente(comboCliente.getValue());
        c.setPalhaco(comboPalhaco.getValue());
        c.setInstrumento(comboInstrumento.getValue());
        c.setDataFinal(campoDataFinal.getValue());
        return c;
    }

    private void preencherFormulario(Contrato c) {
        for (Contratante x : comboContratante.getItems()) {
            if (x.getId() == c.getContratante().getId()) { comboContratante.setValue(x); break; }
        }
        for (Cliente x : comboCliente.getItems()) {
            if (x.getId() == c.getCliente().getId()) { comboCliente.setValue(x); break; }
        }
        for (Palhaco x : comboPalhaco.getItems()) {
            if (x.getId() == c.getPalhaco().getId()) { comboPalhaco.setValue(x); break; }
        }
        for (InstrumentoDeRisada x : comboInstrumento.getItems()) {
            if (x.getId() == c.getInstrumento().getId()) { comboInstrumento.setValue(x); break; }
        }
        campoDataFinal.setValue(c.getDataFinal());
        atualizarValorTotal();
    }

    private void limparFormulario() {
        comboContratante.setValue(null);
        comboCliente.setValue(null);
        comboPalhaco.setValue(null);
        comboInstrumento.setValue(null);
        comboNovoStatus.setValue(null);
        campoDataFinal.setValue(null);
        rotuloValorTotal.setText("Valor total: —");
    }

    //mensagens--------------------------------------------------------------------------------------------------
    private void exibirErro(String mensagem) {
        new Alert(Alert.AlertType.ERROR, mensagem).showAndWait();
    }

    private void exibirSucesso(String mensagem) {
        new Alert(Alert.AlertType.INFORMATION, mensagem).showAndWait();
    }

    private boolean confirmar(String pergunta) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, pergunta, ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> resposta = alerta.showAndWait();
        return resposta.isPresent() && resposta.get() == ButtonType.YES;
    }
}