package com.ultimarisada.view.contratante;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;

import com.ultimarisada.model.Contrato;
import com.ultimarisada.model.StatusContrato;
import com.ultimarisada.service.ContratoService;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;


public class TelaContratantePerfil {

    private static final String CAMINHO_FXML = "/view/contratante/TelaContratantePerfil.fxml";
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private Stage stage;
    private ContratoService contratoService;
    private SessaoContratante sessao;

    @FXML private Label rotuloContratante;
    @FXML private TableView<Contrato> tabelaContratos;
    @FXML private TableColumn<Contrato, Integer> colunaId;
    @FXML private TableColumn<Contrato, String> colunaCliente;
    @FXML private TableColumn<Contrato, String> colunaPalhaco;
    @FXML private TableColumn<Contrato, String> colunaInstrumento;
    @FXML private TableColumn<Contrato, StatusContrato> colunaStatus;
    @FXML private TableColumn<Contrato, String> colunaValorTotal;
    @FXML private TableColumn<Contrato, String> colunaNota;
    @FXML private Button botaoAvaliar;

    public TelaContratantePerfil(Stage stage, SessaoContratante sessao) {
        this.stage = stage;
        this.sessao = sessao;
        this.contratoService = new ContratoService();
    }

    public void exibir() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(CAMINHO_FXML));
            loader.setController(this);
            Parent raiz = loader.load();
            stage.setScene(new Scene(raiz));
            stage.setTitle("Contratante — Meu Perfil");
            stage.sizeToScene();
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao carregar " + CAMINHO_FXML, e);
        }
    }

    @FXML
    private void initialize() {
        rotuloContratante.setText(sessao.getContratanteLogado().getNome());

       
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        
        colunaCliente.setCellValueFactory(dados ->
                new SimpleStringProperty(dados.getValue().getCliente().getNome()));
        colunaPalhaco.setCellValueFactory(dados ->
                new SimpleStringProperty(dados.getValue().getPalhaco().getNome()));
        colunaInstrumento.setCellValueFactory(dados ->
                new SimpleStringProperty(dados.getValue().getInstrumento().getNome()));
        colunaValorTotal.setCellValueFactory(dados ->
                new SimpleStringProperty(String.format(PT_BR, "R$ %.2f",
                        contratoService.calcularValorTotal(dados.getValue()))));
        colunaNota.setCellValueFactory(dados -> {
            Contrato c = dados.getValue();
            return new SimpleStringProperty(c.getAvaliacao() == null
                    ? "—"
                    : String.valueOf(c.getAvaliacao().getValor()));
        });

        tabelaContratos.getSelectionModel().selectedItemProperty()
                .addListener((obs, antigo, novo) -> onLinhaSelecionada(novo));

        botaoAvaliar.setDisable(true);
        carregarMeusContratos();
    }

    
    private void carregarMeusContratos() {
        try {
            tabelaContratos.getItems().setAll(
                    contratoService.listarPorContratante(sessao.getContratanteLogado().getId()));
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }

    
    private void onLinhaSelecionada(Contrato novo) {
        if (novo == null) {
            botaoAvaliar.setDisable(true);
            return;
        }
        boolean statusPermite = novo.getStatus() == StatusContrato.REALIZADA
                || novo.getStatus() == StatusContrato.CANCELADA;
        boolean semAvaliacao = novo.getAvaliacao() == null;
        botaoAvaliar.setDisable(!(statusPermite && semAvaliacao));
    }

    
    @FXML
    private void onAvaliarClicado() {
        Contrato contratoSelecionado = tabelaContratos.getSelectionModel().getSelectedItem();
        if (contratoSelecionado == null) {
            exibirErro("Selecione um contrato na tabela.");
            return;
        }
         
        new TelaContratanteAvaliar(stage, sessao, contratoSelecionado).exibir();
        carregarMeusContratos();
    }

    @FXML
    private void onVoltarClicado() {
        new TelaContratanteMenu(stage, sessao).exibir();
    }

    private void exibirErro(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
