package com.ultimarisada.view.firma;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ultimarisada.exceptions.UltimaRisadaException;
import com.ultimarisada.model.Avaliacao;
import com.ultimarisada.model.Contrato;
import com.ultimarisada.model.Palhaco;
import com.ultimarisada.model.StatusContrato;
import com.ultimarisada.service.AvaliacaoService;
import com.ultimarisada.service.ContratoService;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.util.StringConverter;

public class TelaFirmaAvaliacoes {
    private Stage stage;
    private AvaliacaoService avaliacaoService;
    private ContratoService contratoService;
    private PalhacoService palhacoService;
    private Contrato selecionado;

    @FXML private TextField campoValor;
    @FXML private TextArea campoDescricao;
    @FXML private ComboBox<Palhaco> comboFiltroPalhaco;

    @FXML private TableView<Contrato> tabelaContratos;
    @FXML private TableColumn<Contrato, Integer> colunaIdContrato;
    @FXML private TableColumn<Contrato, String> colunaContratante;
    @FXML private TableColumn<Contrato, String> colunaCliente;
    @FXML private TableColumn<Contrato, String> colunaPalhaco;
    @FXML private TableColumn<Contrato, StatusContrato> colunaStatus;
    @FXML private TableColumn<Contrato, String> colunaNota;
    @FXML private TableColumn<Contrato, String> colunaComentario;

    @FXML private Button botaoRemoverAvaliacao;

    public TelaFirmaAvaliacoes(Stage stage){
        this.stage = stage;
        this.avaliacaoService = new AvaliacaoService();
        this.contratoService = new ContratoService();
        this.palhacoService = new PalhacoService();
        this.selecionado = null;
    }

    public void exibir() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/firma/TelaFirmaAvaliacoes.fxml"));
            loader.setController(this);
            Parent raiz = loader.load();
            stage.setScene(new Scene(raiz));
            stage.setTitle("Firma — Avaliações");
            stage.sizeToScene();
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao carregar /view/firma/TelaFirmaAvaliacoes.fxml", e);
        }
    }

    @FXML private void initialize() {
        comboFiltroPalhaco.setConverter(new StringConverter<>() {
            @Override public String toString(Palhaco p) { return p == null ? "" : p.exibirResumo(); }
            @Override public Palhaco fromString(String s) { return null; }
        });

        colunaIdContrato.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colunaContratante.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getContratante().getNome()));
        colunaCliente.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getCliente().getNome()));
        colunaPalhaco.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getPalhaco().getNome()));

        colunaNota.setCellValueFactory(d -> {
            Avaliacao a = d.getValue().getAvaliacao();
            if (a == null) {
                return new SimpleStringProperty("—");
            }
            return new SimpleStringProperty(String.valueOf(a.getValor()));
        });
        colunaComentario.setCellValueFactory(d -> {
            Avaliacao a = d.getValue().getAvaliacao();
            if (a == null) {
                return new SimpleStringProperty("—");
            }
            return new SimpleStringProperty(a.getDescricao());
        });

        tabelaContratos.getSelectionModel().selectedItemProperty()
            .addListener((obs, antigo, novo) -> onLinhaSelecionada(novo));

        botaoRemoverAvaliacao.setDisable(true);

        carregarPalhacos();
        carregarLista();
    }

    private void carregarPalhacos() {
        try {
            comboFiltroPalhaco.getItems().setAll(palhacoService.listarTodos());
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }

    private void carregarLista(){
        try{
            List<Contrato> todos = contratoService.listarTodos();
            Palhaco filtro = comboFiltroPalhaco.getValue();
            List<Contrato> filtrados = new ArrayList<>();
            for (int i = 0; i < todos.size(); i++) {
                Contrato c = todos.get(i);

                boolean naoPendente = c.getStatus() != StatusContrato.PENDENTE;
                boolean passaFiltro = filtro == null || c.getPalhaco().getId() == filtro.getId();

                if (naoPendente && passaFiltro) {
                    filtrados.add(c);
                }
            }
            tabelaContratos.getItems().setAll(filtrados);
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }

    }

    @FXML private void onFiltroAlterado(){
        carregarLista();
    }

    @FXML private void onLimparFiltroClicado(){
        comboFiltroPalhaco.setValue(null);
    }

    private void onLinhaSelecionada(Contrato novo){
        if(novo == null){ return; }
        selecionado = novo;
        preencherFormulario(novo);
        botaoRemoverAvaliacao.setDisable(novo.getAvaliacao() == null);
    }

    @FXML private void onSalvarClicado() {
        if (selecionado == null) {
            exibirErro("Selecione um contrato na tabela.");
            return;
        }
        Integer valor = lerValor();
        if (valor == null) return;
        String descricao = campoDescricao.getText();

        try {
            if (selecionado.getAvaliacao() == null) {
                avaliacaoService.avaliar(selecionado.getId(),
                    selecionado.getContratante().getId(), valor, descricao);
            } else {
                avaliacaoService.editar(selecionado.getId(), valor, descricao);
            }
            exibirSucesso("Avaliação salva com sucesso. A nota do palhaço foi recalculada.");
            carregarLista();
            limparFormulario();
        } catch (UltimaRisadaException e) {
            exibirErro(e.getMessage());
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }

    @FXML private void onRemoverClicado() {
        if (selecionado == null || selecionado.getAvaliacao() == null) {
            exibirErro("Selecione um contrato avaliado na tabela.");
            return;
        }
        if (confirmar("Remover a avaliação do contrato " + selecionado.getId() + "?")) {
            try {
                avaliacaoService.remover(selecionado.getId());
                exibirSucesso("Avaliação removida com sucesso. A nota do palhaço foi recalculada.");
                carregarLista();
                limparFormulario();
            } catch (SQLException e) {
                exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
            }
        }
    }

    @FXML private void onVoltarClicado() {
        new TelaFirmaMenu(stage).exibir(); // nao temos menu ainda
    }

    private void preencherFormulario(Contrato c) {
        if (c.getAvaliacao() != null) {
            campoValor.setText(String.valueOf(c.getAvaliacao().getValor()));
            campoDescricao.setText(c.getAvaliacao().getDescricao());
        } else {
            campoValor.clear();
            campoDescricao.clear();
        }
    }

    private void limparFormulario() {
        campoValor.clear();
        campoDescricao.clear();
        tabelaContratos.getSelectionModel().clearSelection();
        selecionado = null;
        botaoRemoverAvaliacao.setDisable(true);
    }

    private Integer lerValor() {
        try {
            return Integer.parseInt(campoValor.getText().trim());
        } catch (NumberFormatException e) {
            exibirErro("O campo nota está inválido.");
            return null;
        }
    }
    //mensagens---------------------------------------------------
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
