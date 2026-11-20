package com.ultimarisada.view.contratante;

import java.io.IOException;

import com.ultimarisada.view.TelaInicial;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class TelaContratanteMenu {
    private Stage stage;
    private SessaoContratante sessao;

    @FXML private Label rotuloBoasVindas;

    public TelaContratanteMenu(Stage stage, SessaoContratante sessao) {
        this.stage = stage;
        this.sessao = sessao;
    }

    public void exibir() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/contratante/TelaContratanteMenu.fxml"));
            loader.setController(this);
            Parent raiz = loader.load();
            stage.setScene(new Scene(raiz));
            stage.setTitle("Contratante — Menu");
            stage.sizeToScene();
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao carregar /view/contratante/TelaContratanteMenu.fxml", e);
        }
    }

    @FXML private void initialize() {
        rotuloBoasVindas.setText("Olá, " + sessao.getContratanteLogado().getNome());
    }

    @FXML private void onPerfilClicado() {
        new TelaContratantePerfil(stage, sessao).exibir();
    }

    @FXML private void onClientesClicado() {
        new TelaContratanteClientes(stage, sessao).exibir();
    }

    @FXML private void onNovoContratoClicado() {
        new TelaContratanteNovoContrato(stage, sessao).exibir();
    }

    @FXML private void onVerPalhacosClicado() {
        new TelaContratantePerfilPalhaco(stage, null).exibir();
    }

    @FXML private void onSairClicado() {
        sessao.logout();
        new TelaInicial(stage).exibir();
    }
}