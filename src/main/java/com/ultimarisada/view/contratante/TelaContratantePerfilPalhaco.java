package com.ultimarisada.view.contratante;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.ultimarisada.model.Contrato;
import com.ultimarisada.model.Palhaco;
import com.ultimarisada.service.ContratoService;
import com.ultimarisada.service.PalhacoService;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;


public class TelaContratantePerfilPalhaco {

    private static final String CAMINHO_FXML = "/view/contratante/TelaContratantePerfilPalhaco.fxml";
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private Stage donoStage;
    private Stage dialogo;
    private PalhacoService palhacoService;
    private ContratoService contratoService;
    private Palhaco palhacoInicial;      
    private Palhaco palhacoSelecionado;

    @FXML private ComboBox<Palhaco> comboPalhaco;
    @FXML private Label rotuloNome;
    @FXML private Label rotuloPersonalidade;
    @FXML private Label rotuloCache;
    @FXML private Label rotuloNota;
    @FXML private TableView<Contrato> tabelaAvaliacoes;
    @FXML private TableColumn<Contrato, String> colunaNota;
    @FXML private TableColumn<Contrato, String> colunaComentario;

    public TelaContratantePerfilPalhaco(Stage donoStage, Palhaco palhacoInicial) {
        this.donoStage = donoStage;
        this.palhacoInicial = palhacoInicial;
        this.palhacoService = new PalhacoService();
        this.contratoService = new ContratoService();
    }

    
    public void exibir() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(CAMINHO_FXML));
            loader.setController(this);
            Parent raiz = loader.load();
            dialogo = new Stage();
            dialogo.initOwner(donoStage);
            dialogo.initModality(Modality.APPLICATION_MODAL);
            dialogo.setScene(new Scene(raiz));
            raiz.getStylesheets().add(getClass().getResource("/view/css/estiloPadrao.css").toExternalForm());
            dialogo.setTitle("Perfil do palhaço");
            dialogo.showAndWait();
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao carregar " + CAMINHO_FXML, e);
        }
    }

    public void fechar() {
        dialogo.close();
    }

    @FXML
    private void initialize() {
        comboPalhaco.setConverter(new StringConverter<Palhaco>() {
            @Override
            public String toString(Palhaco p) {
                return p == null ? "" : p.getNome();
            }

            @Override
            public Palhaco fromString(String texto) {
                return null;
            }
        });

        
        colunaNota.setCellValueFactory(dados -> new SimpleStringProperty(
                String.valueOf(dados.getValue().getAvaliacao().getValor())));
        colunaComentario.setCellValueFactory(dados -> {
            String comentario = dados.getValue().getAvaliacao().getDescricao();
            return new SimpleStringProperty(comentario == null ? "" : comentario);
        });

        limparDados();
        carregarPalhacos();

        
        
        if (palhacoInicial != null) {
            for (Palhaco p : comboPalhaco.getItems()) {
                if (p.getId() == palhacoInicial.getId()) {
                    comboPalhaco.setValue(p);
                    break;
                }
            }
        }
    }

    private void carregarPalhacos() {
        try {
            comboPalhaco.getItems().setAll(palhacoService.listarOrdenadoPorNota());
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }

    
    @FXML
    private void onPalhacoSelecionado() {
        palhacoSelecionado = comboPalhaco.getValue();
        if (palhacoSelecionado == null) {
            limparDados();
            return;
        }
        exibirDados(palhacoSelecionado);
        carregarAvaliacoes();
    }

    
    private void exibirDados(Palhaco p) {
        rotuloNome.setText(p.getNome());
        rotuloPersonalidade.setText(p.getPersonalidade());
        rotuloCache.setText(String.format(PT_BR, "R$ %.2f", p.getCache()));
        if (p.getNota() == 0) {
            rotuloNota.setText("Sem avaliações");
        } else {
            rotuloNota.setText(String.format(PT_BR, "%.1f", p.getNota()));
        }
    }

    
    private void carregarAvaliacoes() {
        try {
            List<Contrato> avaliados = contratoService.listarTodos().stream()
                    .filter(c -> c.getPalhaco().getId() == palhacoSelecionado.getId())
                    .filter(c -> c.getAvaliacao() != null)
                    .collect(Collectors.toList());
            tabelaAvaliacoes.getItems().setAll(avaliados);
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }

    @FXML
    private void onFecharClicado() {
        fechar();
    }

    
    private void limparDados() {
        rotuloNome.setText("");
        rotuloPersonalidade.setText("");
        rotuloCache.setText("");
        rotuloNota.setText("");
        tabelaAvaliacoes.getItems().clear();
    }

    private void exibirErro(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.initOwner(dialogo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
