package com.ultimarisada.view.contratante;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import com.ultimarisada.exceptions.ContratoInvalidoException;
import com.ultimarisada.model.Cliente;
import com.ultimarisada.model.Contrato;
import com.ultimarisada.model.InstrumentoDeRisada;
import com.ultimarisada.model.Palhaco;
import com.ultimarisada.service.ClienteService;
import com.ultimarisada.service.ContratoService;
import com.ultimarisada.service.InstrumentoService;
import com.ultimarisada.service.PalhacoService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.StringConverter;


public class TelaContratanteNovoContrato {

    private static final String CAMINHO_FXML = "/view/contratante/TelaContratanteNovoContrato.fxml";
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private Stage stage;
    private ContratoService contratoService;
    private ClienteService clienteService;
    private PalhacoService palhacoService;
    private InstrumentoService instrumentoService;
    private SessaoContratante sessao;

    @FXML private ComboBox<Cliente> comboCliente;
    @FXML private ComboBox<Palhaco> comboPalhaco;
    @FXML private ComboBox<InstrumentoDeRisada> comboInstrumento;
    @FXML private DatePicker campoDataFinal;
    @FXML private Label rotuloValorTotal;

    
    public TelaContratanteNovoContrato(Stage stage, SessaoContratante sessao) {
        this.stage = stage;
        this.sessao = sessao;
        this.contratoService = new ContratoService();
        this.clienteService = new ClienteService();
        this.palhacoService = new PalhacoService();
        this.instrumentoService = new InstrumentoService();
    }

    public void exibir() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(CAMINHO_FXML));
            loader.setController(this);
            Parent raiz = loader.load();
            stage.setScene(new Scene(raiz));
            stage.setTitle("Contratante — Novo Contrato");
            stage.sizeToScene();
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao carregar " + CAMINHO_FXML, e);
        }
    }

    @FXML
    private void initialize() {
        comboCliente.setConverter(new StringConverter<Cliente>() {
            @Override
            public String toString(Cliente c) {
                return c.exibirResumo();
            }

            @Override
            public Cliente fromString(String texto) {
                return null;
            }
        });

        comboPalhaco.setConverter(new StringConverter<Palhaco>() {
            @Override
            public String toString(Palhaco p) {
                if (p == null) {
                    return "";
                }
                return p.exibirResumo();
            }

            @Override
            public Palhaco fromString(String texto) {
                return null;
            }
        });

        comboInstrumento.setConverter(new StringConverter<InstrumentoDeRisada>() {
            @Override
            public String toString(InstrumentoDeRisada i) {
                return i == null ? "" : i.getNome() + " - " + formatarDinheiro(i.getValor());
            }

            @Override
            public InstrumentoDeRisada fromString(String texto) {
                return null;
            }
        });

        rotuloValorTotal.setText("Valor total: —");
        carregarClientes();
        carregarPalhacos();
    }

    
    private void carregarClientes() {
        try {
            List<Cliente> clientes = clienteService
                    .listarPorContratante(sessao.getContratanteLogado().getId());
            comboCliente.getItems().setAll(clientes);
            if (clientes.isEmpty()) {
                exibirErro("Você ainda não tem clientes. Use o botão Novo Cliente.");
            }
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
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
        Palhaco palhaco = comboPalhaco.getValue();
        if (palhaco == null) {
            comboInstrumento.getItems().clear();
            comboInstrumento.setValue(null);
            atualizarValorTotal();
            return;
        }
        try {
            comboInstrumento.getItems().setAll(instrumentoService.listarPorPalhaco(palhaco.getId()));
            comboInstrumento.setValue(null);
            atualizarValorTotal();
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }

    
    @FXML
    private void onInstrumentoSelecionado() {
        atualizarValorTotal();
    }

    
    private void atualizarValorTotal() {
        Palhaco palhaco = comboPalhaco.getValue();
        InstrumentoDeRisada instrumento = comboInstrumento.getValue();
        if (palhaco == null || instrumento == null) {
            rotuloValorTotal.setText("Valor total: —");
            return;
        }
        Contrato temporario = new Contrato();
        temporario.setPalhaco(palhaco);
        temporario.setInstrumento(instrumento);
        double total = contratoService.calcularValorTotal(temporario);
        rotuloValorTotal.setText("Valor total: " + formatarDinheiro(total));
    }

    
    @FXML
    private void onVerPerfilPalhacoClicado() {
        if (comboPalhaco.getValue() == null) {
            exibirErro("Escolha um palhaço para ver o perfil.");
            return;
        }
        new TelaContratantePerfilPalhaco(stage, comboPalhaco.getValue()).exibir();
    }

    @FXML
    private void onNovoClienteClicado() {
        new TelaContratanteClientes(stage, sessao).exibir();
    }

    
    @FXML
    private void onConfirmarClicado() {
        Contrato formulario = lerFormulario();
        if (formulario == null) {
            return;
        }
        try {
            Contrato criado = contratoService.solicitar(
                    sessao.getContratanteLogado().getId(),
                    formulario.getCliente().getId(),
                    formulario.getPalhaco().getId(),
                    formulario.getInstrumento().getId(),
                    formulario.getDataFinal());
            double total = contratoService.calcularValorTotal(criado);
            exibirSucesso("Contrato criado com sucesso. Valor total: " + formatarDinheiro(total) + ".");
            new TelaContratanteMenu(stage, sessao).exibir();
        } catch (ContratoInvalidoException e) {
            exibirErro(e.getMessage());
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }

    @FXML
    private void onVoltarClicado() {
        new TelaContratanteMenu(stage, sessao).exibir();
    }

    
    private Contrato lerFormulario() {
        if (comboCliente.getValue() == null) {
            exibirErro("Selecione o cliente.");
            return null;
        }
        if (comboPalhaco.getValue() == null) {
            exibirErro("Selecione o palhaço.");
            return null;
        }
        if (comboInstrumento.getValue() == null) {
            exibirErro("Escolha um instrumento: ele é obrigatório.");
            return null;
        }
        if (campoDataFinal.getValue() == null) {
            exibirErro("O campo data final está inválido.");
            return null;
        }
        Contrato temporario = new Contrato();
        temporario.setContratante(sessao.getContratanteLogado());
        temporario.setCliente(comboCliente.getValue());
        temporario.setPalhaco(comboPalhaco.getValue());
        temporario.setInstrumento(comboInstrumento.getValue());
        temporario.setDataFinal(campoDataFinal.getValue());
        return temporario;
    }

    private String formatarDinheiro(double valor) {
        return String.format(PT_BR, "R$ %.2f", valor);
    }

    private void exibirErro(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    private void exibirSucesso(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
