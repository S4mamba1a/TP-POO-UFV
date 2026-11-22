package com.ultimarisada.view;
import java.io.IOException;

import com.ultimarisada.view.contratante.TelaContratanteLogin;
import com.ultimarisada.view.firma.TelaFirmaMenu;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TelaInicial {
    private Stage stage;
    public TelaInicial(Stage stage){
        this.stage = stage;
    }
    public void exibir(){
        try{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaInicial.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.setTitle("A Última Risada");
        stage.sizeToScene();
        stage.show();
        }
        catch (IOException e) {
            throw new IllegalStateException("Erro ao carregar /view/TelaInicial.fxml", e);
        }
    }
    @FXML 
    private void onEscolherFirmaClicado(){
        new TelaFirmaMenu(stage).exibir();
    }
    @FXML 
    private void onEscolherContratanteClicado(){
        new TelaContratanteLogin(stage).exibir();
    }
}
