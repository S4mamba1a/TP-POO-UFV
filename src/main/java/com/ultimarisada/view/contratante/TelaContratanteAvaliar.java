package com.ultimarisada.view.contratante;
import java.io.IOException;
import java.sql.SQLException;

import com.ultimarisada.exceptions.AcessoNegadoException;
import com.ultimarisada.exceptions.AvaliacaoDuplicadaException;
import com.ultimarisada.exceptions.OperacaoNaoPermitidaException;
import com.ultimarisada.exceptions.ValorDeAvaliacaoInvalidoException;
import com.ultimarisada.model.Contrato;
import com.ultimarisada.service.AvaliacaoService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class TelaContratanteAvaliar {

    private static final String CAMINHO_FXML = "/view/contratante/TelaContratanteAvaliar.fxml";

    private Stage donoStage;
    private Stage dialogo;
    private AvaliacaoService avaliacaoService;
    private SessaoContratante sessao;
    private Contrato contratoAlvo;

    @FXML private Label rotuloResumo;
    @FXML private TextField campoValor;
    @FXML private TextArea campoDescricao;

    public TelaContratanteAvaliar(Stage donoStage, SessaoContratante sessao, Contrato contratoAlvo) {
        this.donoStage = donoStage;
        this.sessao = sessao;
        this.contratoAlvo = contratoAlvo;
        this.avaliacaoService = new AvaliacaoService();
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
            dialogo.setTitle("Avaliar contrato");
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
        rotuloResumo.setText("Palhaço: " + contratoAlvo.getPalhaco().exibirResumo()
                + " | Cliente: " + contratoAlvo.getCliente().exibirResumo()
                + " | Status: " + contratoAlvo.getStatus());
    }

    
    @FXML
    private void onConfirmarClicado() {
        Integer valor = lerValor();
        if (valor == null) {
            return;
        }
        String descricao = campoDescricao.getText();
        try {
            avaliacaoService.avaliar(contratoAlvo.getId(),
                    sessao.getContratanteLogado().getId(), valor, descricao);
            exibirSucesso("Avaliação registrada com sucesso.");
            fechar();
        } catch (AcessoNegadoException e) {              
            exibirErro(e.getMessage());
        } catch (OperacaoNaoPermitidaException e) {      
            exibirErro(e.getMessage());
        } catch (AvaliacaoDuplicadaException e) {        
            exibirErro(e.getMessage());
        } catch (ValorDeAvaliacaoInvalidoException e) {  
            exibirErro(e.getMessage());
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }

    @FXML
    private void onCancelarClicado() {
        fechar();
    }

    
    private Integer lerValor() {
        String texto = campoValor.getText();
        if (texto == null || texto.trim().isEmpty()) {
            exibirErro("O campo nota está inválido.");
            return null;
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            exibirErro("O campo nota está inválido.");
            return null;
        }
    }

    private void exibirErro(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.initOwner(dialogo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    private void exibirSucesso(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.initOwner(dialogo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
