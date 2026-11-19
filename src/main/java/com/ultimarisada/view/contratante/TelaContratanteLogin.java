package com.ultimarisada.view.contratante;
import java.io.IOException;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import com.ultimarisada.model.Contratante;
import com.ultimarisada.service.ContratanteService;
import com.ultimarisada.exceptions.CredenciaisInvalidasException;
import com.ultimarisada.view.TelaInicial;
 

public class TelaContratanteLogin {

    private Stage stage;
    private ContratanteService contratanteService;

    @FXML private TextField campoNome;
    @FXML private PasswordField campoSenha;

    public TelaContratanteLogin(Stage stage) {
        this.stage = stage;
        this.contratanteService = new ContratanteService();
    }

    public void exibir() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/contratante/TelaContratanteLogin.fxml"));
            loader.setController(this);
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Login do Contratante");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar /view/contratante/TelaContratanteLogin.fxml", e);
        }
    }

    @FXML
    private void onEntrarClicado() {
        String nome = campoNome.getText();
        String senha = campoSenha.getText();

        // Campos vazios: não chama o service
        if (nome == null || nome.isBlank() || senha == null || senha.isBlank()) {
            exibirErro("Informe nome e senha.");
            return;
        }

        try {
            Contratante contratante = contratanteService.autenticar(nome, senha);
            SessaoContratante sessao = new SessaoContratante();
            sessao.login(contratante);
            new TelaContratanteMenu(stage, sessao).exibir();

        } catch (CredenciaisInvalidasException e) {
            exibirErro("Nome ou senha incorretos.");
            campoSenha.clear();
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados: " + e.getMessage());
        }
    }

    @FXML
    private void onCadastrarSeClicado() {
        new TelaContratanteCadastro(stage).exibir();
    }

    @FXML
    private void onVoltarClicado() {
        new TelaInicial(stage).exibir();
    }

    private void exibirErro(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Erro");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
