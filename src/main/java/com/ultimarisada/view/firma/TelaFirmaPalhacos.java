package com.ultimarisada.view.firma;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.ultimarisada.exceptions.PalhacoEmUsoException;
import com.ultimarisada.model.Palhaco;
import com.ultimarisada.service.PalhacoService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class TelaFirmaPalhacos {
    private Stage stage;
    private PalhacoService palhacoService;
    private Palhaco selecionado;

    @FXML private TextField campoNome;
    @FXML private DatePicker campoDataNascimento;
    @FXML private TextField campoPersonalidade;
    @FXML private TextField campoCache;
    @FXML private Label rotuloNota;
    @FXML private TextField campoBuscaNome;
    @FXML private TextField campoBuscaPersonalidade;
    @FXML private TextField campoBuscaCacheMaximo;
    @FXML private TableView<Palhaco> tabelaPalhacos;
    @FXML private TableColumn<Palhaco, Integer> colunaId;
    @FXML private TableColumn<Palhaco, String> colunaNome;
    @FXML private TableColumn<Palhaco, LocalDate> colunaDataNascimento;
    @FXML private TableColumn<Palhaco, String> colunaPersonalidade;
    @FXML private TableColumn<Palhaco, Double> colunaCache;
    @FXML private TableColumn<Palhaco, Double> colunaNota;
    @FXML private Button botaoRemover;

    public TelaFirmaPalhacos(Stage stage) {
        this.stage = stage;
        this.palhacoService = new PalhacoService();
        this.selecionado = null;
    }

    public void exibir() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/firma/TelaFirmaPalhacos.fxml"));
            loader.setController(this);
            Parent raiz = loader.load();
            stage.setScene(new Scene(raiz));
            stage.setTitle("Firma — Palhaços");
            stage.sizeToScene();
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao carregar /view/firma/TelaFirmaPalhacos.fxml", e);
        }
    }

    @FXML
    private void initialize() {
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaDataNascimento.setCellValueFactory(new PropertyValueFactory<>("dataNascimento"));
        colunaPersonalidade.setCellValueFactory(new PropertyValueFactory<>("personalidade"));
        colunaCache.setCellValueFactory(new PropertyValueFactory<>("cache"));
        colunaNota.setCellValueFactory(new PropertyValueFactory<>("nota"));

        tabelaPalhacos.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> onLinhaSelecionada(novo));

        botaoRemover.setDisable(true);
        carregarLista();
    }

    private void carregarLista() {
        try {
            preencherTabela(palhacoService.listarTodos());
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }

    private void preencherTabela(List<Palhaco> palhacos) {
        tabelaPalhacos.getItems().setAll(palhacos);
    }

    @FXML
    private void onBuscarClicado() {
        String nome = campoBuscaNome.getText().trim();
        String personalidade = campoBuscaPersonalidade.getText().trim();
        String cacheTexto = campoBuscaCacheMaximo.getText().trim();

        try {
            if (!nome.isEmpty()) {
                preencherTabela(palhacoService.buscarPalhaco(nome));
            } else if (!personalidade.isEmpty() || !cacheTexto.isEmpty()) {
                double cacheMaximo = Double.MAX_VALUE;
                if (!cacheTexto.isEmpty()) {
                    try {
                        cacheMaximo = Double.parseDouble(cacheTexto.replace(",", "."));
                    } catch (NumberFormatException e) {
                        exibirErro("O campo cachê máximo está inválido.");
                        return;
                    }
                }
                preencherTabela(palhacoService.buscarPalhaco(personalidade, cacheMaximo));
            } else {
                carregarLista();
            }
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }

    @FXML
    private void onLimparBuscaClicado() {
        campoBuscaNome.clear();
        campoBuscaPersonalidade.clear();
        campoBuscaCacheMaximo.clear();
        carregarLista();
    }

    @FXML
    private void onOrdenarPorNotaClicado() {
        try {
            preencherTabela(palhacoService.listarOrdenadoPorNota());
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }

    private void onLinhaSelecionada(Palhaco novo) {
        if (novo == null) return;
        selecionado = novo;
        preencherFormulario(novo);
        botaoRemover.setDisable(false);
    }

    @FXML
    private void onNovoClicado() {
        selecionado = null;
        limparFormulario();
        tabelaPalhacos.getSelectionModel().clearSelection();
        botaoRemover.setDisable(true);
    }

    @FXML
    private void onSalvarClicado() {
        Palhaco p = lerFormulario();
        if (p == null) return;

        try {
            if (selecionado == null) {
                palhacoService.cadastrar(p);
                exibirSucesso("Palhaço cadastrado com sucesso.");
            } else {
                palhacoService.editar(p);
                exibirSucesso("Palhaço atualizado com sucesso.");
            }
            carregarLista();
            onNovoClicado();
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }

    @FXML
    private void onRemoverClicado() {
        if (selecionado == null) {
            exibirErro("Selecione um palhaço na tabela para remover.");
            return;
        }
        if (confirmar("Remover o palhaço '" + selecionado.getNome() + "'?")) {
            try {
                palhacoService.remover(selecionado.getId());
                exibirSucesso("Palhaço removido com sucesso.");
                carregarLista();
                onNovoClicado();
            } catch (PalhacoEmUsoException e) {
                exibirErro(e.getMessage());
            } catch (SQLException e) {
                exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
            }
        }
    }

    @FXML
    private void onVoltarClicado() {
        new TelaFirmaMenu(stage).exibir();        //TelaFirmaMenu ainda não existe
    }

    private Palhaco lerFormulario() {
        String nome = campoNome.getText().trim();
        LocalDate data = campoDataNascimento.getValue();
        String personalidade = campoPersonalidade.getText();
        double cache;

        if (nome.isEmpty()) {
            exibirErro("O campo nome está inválido.");
            return null;
        }
        if (data == null) {
            exibirErro("O campo data de nascimento está inválido.");
            return null;
        }
        try {
            cache = Double.parseDouble(campoCache.getText().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            exibirErro("O campo cachê está inválido.");
            return null;
        }

        Palhaco p = new Palhaco(nome, data, personalidade, cache);
        if (selecionado != null) {
            p.setId(selecionado.getId());
        }
        return p;
    }

    private void preencherFormulario(Palhaco p) {
        campoNome.setText(p.getNome());
        campoDataNascimento.setValue(p.getDataNascimento());
        campoPersonalidade.setText(p.getPersonalidade());
        campoCache.setText(String.valueOf(p.getCache()));
        rotuloNota.setText(String.valueOf(p.getNota()));
    }

    private void limparFormulario() {
        campoNome.clear();
        campoDataNascimento.setValue(null);
        campoPersonalidade.clear();
        campoCache.clear();
        rotuloNota.setText("");
    }

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