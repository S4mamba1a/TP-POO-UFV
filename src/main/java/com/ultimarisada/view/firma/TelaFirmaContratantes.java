package com.ultimarisada.view.firma;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import com.ultimarisada.exceptions.ContratanteEmUsoException;
import com.ultimarisada.exceptions.ContratanteJaCadastradoException;
import com.ultimarisada.exceptions.MenorDeIdadeException;
import com.ultimarisada.model.Contratante;
import com.ultimarisada.service.ContratanteService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;


public class TelaFirmaContratantes {
    private Stage stage;
    private ContratanteService contratanteService;
    private Contratante selecionado; // null indica modo novo

    @FXML private TextField campoNome;
    @FXML private DatePicker campoDataNascimento;
    @FXML private PasswordField campoSenha;
    @FXML private TableView<Contratante> tabelaContratantes;
    @FXML private TableColumn<Contratante, Integer> colunaId;
    @FXML private TableColumn<Contratante, String> colunaNome;
    @FXML private TableColumn<Contratante, LocalDate> colunaDataNascimento;
    @FXML private Button botaoRemover;

    public TelaFirmaContratantes(Stage stage) {
        this.stage = stage;
        this.contratanteService = new ContratanteService();
        this.selecionado = null;
    }

    public void exibir() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/firma/TelaFirmaContratantes.fxml"));
            loader.setController(this);
            Parent raiz = loader.load();
            stage.setScene(new Scene(raiz));
            stage.setTitle("Gerenciador de Contratantes");
            stage.sizeToScene();
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao carregar /view/firma/TelaFirmaContratantes.fxml", e);
        }
    }

    @FXML
    private void initialize() {
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaDataNascimento.setCellValueFactory(new PropertyValueFactory<>("dataNascimento"));

        tabelaContratantes.getSelectionModel().selectedItemProperty()
                .addListener((obs, antigo, novo) -> onLinhaSelecionada(novo));

        botaoRemover.setDisable(true);
        carregarLista();
    }

    private void carregarLista() {
        try {
            tabelaContratantes.getItems().setAll(contratanteService.listar());
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }

    private void onLinhaSelecionada(Contratante novo) {
        if (novo == null) return;
        selecionado = novo;
        preencherFormulario(novo);
        botaoRemover.setDisable(false);
    }

    @FXML
    private void onNovoClicado() {
        selecionado = null;
        limparFormulario();
        tabelaContratantes.getSelectionModel().clearSelection();
        botaoRemover.setDisable(true);
        campoNome.requestFocus();
    }

    @FXML
    private void onSalvarClicado() {
        Contratante c = lerFormulario();
        if (c == null) return;

        try {
            if (selecionado == null) {
                contratanteService.cadastrar(c);
                exibirSucesso("Contratante cadastrado com sucesso.");
            } else {
                contratanteService.editar(c);
                exibirSucesso("Contratante atualizado com sucesso.");
            }
            carregarLista();
            onNovoClicado();
        } catch (ContratanteJaCadastradoException e) {
            exibirErro(e.getMessage());
        } catch (MenorDeIdadeException e) {
            exibirErro(e.getMessage());
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }

    @FXML
    private void onRemoverClicado() {
        if (selecionado == null) {
            exibirErro("Selecione um contratante na tabela para remover.");
            return;
        }
        if (confirmar("Remover o contratante '" + selecionado.getNome() + "'?")) {
            try {
                contratanteService.remover(selecionado.getId());
                exibirSucesso("Contratante removido com sucesso.");
                carregarLista();
                onNovoClicado();
            } catch (ContratanteEmUsoException e) {
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

    private Contratante lerFormulario() {
        String nome = campoNome.getText() == null ? "" : campoNome.getText().trim();
        String senha = campoSenha.getText() == null ? "" : campoSenha.getText().trim();
        LocalDate data = campoDataNascimento.getValue();

        if (nome.isEmpty()) {
            exibirErro("O campo nome está inválido.");
            return null;
        }
        if (senha.isEmpty()) {
            exibirErro("O campo senha está inválido.");
            return null;
        }
        if (data == null) {
            exibirErro("O campo data de nascimento está inválido.");
            return null;
        }

        Contratante c = new Contratante(nome, data, senha);
        if (selecionado != null) {
            c.setId(selecionado.getId()); // para o editar saber qual registro alterar
        }
        return c;
    }

    private void preencherFormulario(Contratante c) {
        campoNome.setText(c.getNome());
        campoDataNascimento.setValue(c.getDataNascimento());
        campoSenha.setText(c.getSenha());
    }

    private void limparFormulario() {
        campoNome.clear();
        campoDataNascimento.setValue(null);
        campoSenha.clear();
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
