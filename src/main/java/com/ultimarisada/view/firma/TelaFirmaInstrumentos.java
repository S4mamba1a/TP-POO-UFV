package com.ultimarisada.view.firma;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.ultimarisada.exceptions.InstrumentoEmUsoException;
import com.ultimarisada.model.InstrumentoDeRisada;
import com.ultimarisada.model.Palhaco;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class TelaFirmaInstrumentos {
    private Stage stage;
    private InstrumentoService instrumentoService;
    private PalhacoService palhacoService;
    private InstrumentoDeRisada selecionado;
    private Palhaco palhacoDonoDoSelecionado;
    private Map<Integer, Palhaco> donosPorIdInstrumento;

    @FXML private TextField campoNome;
    @FXML private TextField campoDescricao;
    @FXML private TextField campoValor;
    @FXML private ComboBox<Palhaco> comboPalhacoDono;
    @FXML private ComboBox<Palhaco> comboFiltroPalhaco;
    @FXML private TableView<InstrumentoDeRisada> tabelaInstrumentos;
    @FXML private TableColumn<InstrumentoDeRisada, Integer> colunaId;
    @FXML private TableColumn<InstrumentoDeRisada, String> colunaNome;
    @FXML private TableColumn<InstrumentoDeRisada, String> colunaDescricao;
    @FXML private TableColumn<InstrumentoDeRisada, Double> colunaValor;
    @FXML private TableColumn<InstrumentoDeRisada, String> colunaPalhacoDono;
    @FXML private Button botaoRemover;

    public TelaFirmaInstrumentos(Stage stage) {
        this.stage = stage;
        this.instrumentoService = new InstrumentoService();
        this.palhacoService = new PalhacoService();
        this.selecionado = null;
        this.palhacoDonoDoSelecionado = null;
        this.donosPorIdInstrumento = new HashMap<>();
    }

    public void exibir() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/firma/TelaFirmaInstrumentos.fxml"));
            loader.setController(this);
            Parent raiz = loader.load();
            stage.setScene(new Scene(raiz));
            stage.setTitle("Firma — Instrumentos");
            stage.sizeToScene();
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao carregar /view/firma/TelaFirmaInstrumentos.fxml", e);
        }
    }

    @FXML
    private void initialize() {
        StringConverter<Palhaco> conversor = new StringConverter<Palhaco>() {
            @Override
            public String toString(Palhaco p) {
                return p == null ? "" : p.exibirResumo();
            }
            @Override
            public Palhaco fromString(String texto) {
                return null;
            }
        };
        comboPalhacoDono.setConverter(conversor);
        comboFiltroPalhaco.setConverter(conversor);

        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colunaValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colunaPalhacoDono.setCellValueFactory(dados -> {
            Palhaco dono = donosPorIdInstrumento.get(dados.getValue().getId());
            return new SimpleStringProperty(dono == null ? "" : dono.getNome());
        });

        tabelaInstrumentos.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> onLinhaSelecionada(novo));

        botaoRemover.setDisable(true);
        carregarPalhacos();
        carregarLista();
    }

    private void carregarPalhacos() {
        try {
            List<Palhaco> palhacos = palhacoService.listarTodos();
            comboPalhacoDono.getItems().setAll(palhacos);
            comboFiltroPalhaco.getItems().setAll(palhacos);
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }

    private void carregarLista() {
        donosPorIdInstrumento.clear();
        List<InstrumentoDeRisada> instrumentos = new ArrayList<>();
        try {
            Palhaco filtrado = comboFiltroPalhaco.getValue();
            if (filtrado == null) {
                for (Palhaco p : palhacoService.listarTodos()) {
                    for (InstrumentoDeRisada i : p.getInstrumentos()) {
                        instrumentos.add(i);
                        donosPorIdInstrumento.put(i.getId(), p);
                    }
                }
            } else {
                instrumentos = instrumentoService.listarPorPalhaco(filtrado.getId());
                for (InstrumentoDeRisada i : instrumentos) {
                    donosPorIdInstrumento.put(i.getId(), filtrado);
                }
            }
            tabelaInstrumentos.getItems().setAll(instrumentos);
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }

    @FXML
    private void onFiltroAlterado() {
        carregarLista();
    }

    @FXML
    private void onLimparFiltroClicado() {
        // setValue(null) já dispara o onAction do filtro, que recarrega a lista
        comboFiltroPalhaco.setValue(null);
    }

    private void onLinhaSelecionada(InstrumentoDeRisada novo) {
        if (novo == null) return;
        selecionado = novo;
        palhacoDonoDoSelecionado = donosPorIdInstrumento.get(novo.getId());
        preencherFormulario(novo);

        // seleciona o dono procurando pelo id (o model não tem equals)
        comboPalhacoDono.setValue(null);
        if (palhacoDonoDoSelecionado != null) {
            for (Palhaco p : comboPalhacoDono.getItems()) {
                if (p.getId() == palhacoDonoDoSelecionado.getId()) {
                    comboPalhacoDono.setValue(p);
                }
            }
        }
        comboPalhacoDono.setDisable(true);
        botaoRemover.setDisable(false);
    }

    @FXML
    private void onNovoClicado() {
        selecionado = null;
        palhacoDonoDoSelecionado = null;
        limparFormulario();
        tabelaInstrumentos.getSelectionModel().clearSelection();
        comboPalhacoDono.setDisable(false);
        botaoRemover.setDisable(true);
    }

    @FXML
    private void onSalvarClicado() {
        InstrumentoDeRisada i = lerFormulario();
        if (i == null) return;

        try {
            if (selecionado == null) {
                instrumentoService.cadastrar(i, comboPalhacoDono.getValue().getId());
                exibirSucesso("Instrumento cadastrado com sucesso.");
            } else {
                instrumentoService.editar(i);
                exibirSucesso("Instrumento atualizado com sucesso.");
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
            exibirErro("Selecione um instrumento na tabela para remover.");
            return;
        }
        if (confirmar("Remover o instrumento '" + selecionado.getNome() + "'?")) {
            try {
                instrumentoService.remover(selecionado.getId());
                exibirSucesso("Instrumento removido com sucesso.");
                carregarLista();
                onNovoClicado();
            } catch (InstrumentoEmUsoException e) {
                exibirErro(e.getMessage());
            } catch (SQLException e) {
                exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
            }
        }
    }

    @FXML
    private void onVoltarClicado() {
        new TelaFirmaMenu(stage).exibir();
    }

    private InstrumentoDeRisada lerFormulario() {
        String nome = campoNome.getText().trim();
        String descricao = campoDescricao.getText();
        double valor;

        if (nome.isEmpty()) {
            exibirErro("O campo nome está inválido.");
            return null;
        }
        try {
            valor = Double.parseDouble(campoValor.getText().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            exibirErro("O campo valor está inválido.");
            return null;
        }
        if (selecionado == null && comboPalhacoDono.getValue() == null) {
            exibirErro("Selecione o palhaço dono do instrumento.");
            return null;
        }

        InstrumentoDeRisada i = new InstrumentoDeRisada(valor, nome, descricao);
        if (selecionado != null) {
            i.setId(selecionado.getId());
        }
        return i;
    }

    private void preencherFormulario(InstrumentoDeRisada i) {
        campoNome.setText(i.getNome());
        campoDescricao.setText(i.getDescricao());
        campoValor.setText(String.valueOf(i.getValor()));
    }

    private void limparFormulario() {
        campoNome.clear();
        campoDescricao.clear();
        campoValor.clear();
        comboPalhacoDono.setValue(null);
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