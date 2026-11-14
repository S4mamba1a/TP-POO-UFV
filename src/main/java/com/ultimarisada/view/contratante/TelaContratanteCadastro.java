package com.ultimarisada.view.contratante;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

import com.ultimarisada.exceptions.ContratanteJaCadastradoException;
import com.ultimarisada.model.Contratante;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
public class TelaContratanteCadastro {
    private Stage stage;
    @FXML private TextField campoNome;
    @FXML private DatePicker campoDataNascimento;
    @FXML private PasswordField campoSenha;

    public TelaContratanteCadastro(Stage stage){
        this.stage = stage;
    }

    public void exibir(){
        try{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/contratante/TelaContratanteCadastro.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.setTitle("Cadastro do Contratante");
        stage.sizeToScene();
        stage.show();
        }
        catch (IOException e) {
            throw new IllegalStateException("Erro ao carregar /view/contratante/TelaContratanteCadastro.fxml", e);
        }
    }
    @FXML
    private void onConfirmarClicando(){
        //pega os textos
        String nome = campoNome.getText().trim();
        String senha = campoSenha.getText();
        LocalDate dataNascimento = campoDataNascimento.getValue();

        if (nome.isEmpty()) {
            errado("Campo de nome invalido!");
            return;
        }
        if (dataNascimento == null) {
            errado("Data de Nascimento invalida!");
            return;
        }
        if (senha.isEmpty()) {
            errado("Senha Invalida");
            return;
        }

        try {
            Contratante contratante = contratanteService.cadastrarProprio(nome, dataNascimento, senha);

            SessaoContratante sessao = new SessaoContratante();
            sessao.login(contratante);
            //new TelaContratanteMenu(stage, sessao).exibir();

        } catch (ContratanteJaCadastradoException e) { 
            errado(e.getMessage());
        } catch (SQLException e) {
            errado("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }
    @FXML
    private void onCancelarClicado() {
        //new TelaContratanteLogin(stage).exibir();
    }

    private void errado(String mensagem){
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensagem);
        alerta.showAndWait();
    }
}

