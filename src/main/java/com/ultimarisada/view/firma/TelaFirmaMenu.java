package com.ultimarisada.view.firma;

import java.io.IOException;

import com.ultimarisada.view.TelaInicial;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TelaFirmaMenu {
    private Stage stage;
    public TelaFirmaMenu(Stage stage){
        this.stage = stage;
    }
    public void exibir() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/firma/TelaFirmaMenu.fxml"));
            loader.setController(this);
            Parent raiz = loader.load();
            stage.setScene(new Scene(raiz));
            raiz.getStylesheets().add(getClass().getResource("/view/css/estiloPadrao.css").toExternalForm());
            stage.setTitle("Firma");
            stage.sizeToScene();
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao carregar /view/firma/TelaFirmaMenu.fxml", e);
        }
    }
    
    @FXML private void onContratantesClicado() {
        new TelaFirmaContratantes(stage).exibir();
    }

    @FXML private void onClientesClicado() {
        new TelaFirmaClientes(stage).exibir();
    }

    @FXML private void onPalhacosClicado() {
        new TelaFirmaPalhacos(stage).exibir();
    }

    @FXML private void onInstrumentosClicado() {
        new TelaFirmaInstrumentos(stage).exibir();
    }

    @FXML private void onContratosClicado() {
        new TelaFirmaContratos(stage).exibir();
    }

    @FXML private void onAvaliacoesClicado() {
        new TelaFirmaAvaliacoes(stage).exibir();
    }

    @FXML private void onSairClicado() {
        new TelaInicial(stage).exibir();
    }
    
}