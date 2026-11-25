package com.ultimarisada.view.firma;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import com.ultimarisada.exceptions.ClienteEmUsoException;
import com.ultimarisada.exceptions.ClienteJaCadastradoException;
import com.ultimarisada.exceptions.ClienteSemContratanteException;
import com.ultimarisada.exceptions.MenorDeIdadeException;
import com.ultimarisada.model.Cliente;
import com.ultimarisada.model.Contratante;
import com.ultimarisada.service.ClienteService;
import com.ultimarisada.service.ContratanteService;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;


public class TelaFirmaClientes {
    private Stage stage;
    private ClienteService clienteService;
    private ContratanteService contratanteService; // só para preencher as caixas de contratante
    private Cliente selecionado; // null indica modo novo

    @FXML private TextField campoNome;
    @FXML private DatePicker campoDataNascimento;
    @FXML private TextField campoDescricaoFisica;
    @FXML private TextField campoEndereco;
    @FXML private TextField campoLocalDeTrabalho;
    @FXML private ComboBox<Contratante> comboContratante;       // dono do cliente (obrigatório)
    @FXML private ComboBox<Contratante> comboFiltroContratante; // filtro da tabela (null = todos)
    @FXML private TableView<Cliente> tabelaClientes;
    @FXML private TableColumn<Cliente, Integer> colunaId;
    @FXML private TableColumn<Cliente, String> colunaNome;
    @FXML private TableColumn<Cliente, LocalDate> colunaDataNascimento;
    @FXML private TableColumn<Cliente, String> colunaDescricaoFisica;
    @FXML private TableColumn<Cliente, String> colunaEndereco;
    @FXML private TableColumn<Cliente, String> colunaLocalDeTrabalho;
    @FXML private TableColumn<Cliente, String> colunaContratante; // nome do contratante dono (coluna calculada)
    @FXML private Button botaoRemover;

    public TelaFirmaClientes(Stage stage) {
        this.stage = stage;
        this.clienteService = new ClienteService();
        this.contratanteService = new ContratanteService();
        this.selecionado = null;
    }

    public void exibir() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/firma/TelaFirmaClientes.fxml"));
            loader.setController(this);
            Parent raiz = loader.load();
            stage.setScene(new Scene(raiz));
            stage.setTitle("Firma — Clientes");
            stage.sizeToScene();
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao carregar /view/firma/TelaFirmaClientes.fxml", e);
        }
    }

    @FXML
    private void initialize() {
        StringConverter<Contratante> conversor = new StringConverter<>() {
            @Override
            public String toString(Contratante c) {
                return c == null ? "" : c.exibirResumo();
            }

            @Override
            public Contratante fromString(String texto) {
                return null; // as caixas não são editáveis
            }
        };
        comboContratante.setConverter(conversor);
        comboFiltroContratante.setConverter(conversor);

        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaDataNascimento.setCellValueFactory(new PropertyValueFactory<>("dataNascimento"));
        colunaDescricaoFisica.setCellValueFactory(new PropertyValueFactory<>("descricaoFisica"));
        colunaEndereco.setCellValueFactory(new PropertyValueFactory<>("endereco"));
        colunaLocalDeTrabalho.setCellValueFactory(new PropertyValueFactory<>("localDeTrabalho"));
        colunaContratante.setCellValueFactory(
                dados -> new SimpleStringProperty(dados.getValue().getContratante().getNome()));


        tabelaClientes.getSelectionModel().selectedItemProperty()
                .addListener((obs, antigo, novo) -> onLinhaSelecionada(novo));

        botaoRemover.setDisable(true);
        carregarContratantes();
        carregarLista();
    }

    private void carregarContratantes() {
        try {
            var contratantes = contratanteService.listar();
            comboContratante.getItems().setAll(contratantes);
            comboFiltroContratante.getItems().setAll(contratantes);
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }

    private void carregarLista() {
        try {
            Contratante filtro = comboFiltroContratante.getValue();
            if (filtro == null) {
                tabelaClientes.getItems().setAll(clienteService.listarTodos());
            } else {
                tabelaClientes.getItems().setAll(clienteService.listarPorContratante(filtro.getId()));
            }
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
        comboFiltroContratante.setValue(null);
    }

    private void onLinhaSelecionada(Cliente novo) {
        if (novo == null) return;
        selecionado = novo;
        preencherFormulario(novo);
        botaoRemover.setDisable(false); // o contratante dono pode ser trocado
    }

    @FXML
    private void onNovoClicado() {
        selecionado = null;
        limparFormulario();
        tabelaClientes.getSelectionModel().clearSelection();
        botaoRemover.setDisable(true);
    }

    @FXML
    private void onSalvarClicado() {
        Cliente c = lerFormulario();
        if (c == null) return;

        try {
            if (selecionado == null) {
                clienteService.cadastrar(c);
                exibirSucesso("Cliente cadastrado com sucesso.");
            } else {
                clienteService.editar(c);
                exibirSucesso("Cliente atualizado com sucesso.");
            }
            carregarLista();
            onNovoClicado();
        } catch (ClienteJaCadastradoException e) {
            // FA1: mostra a mensagem e mantém o formulário preenchido
            exibirErro(e.getMessage());
        } catch (ClienteSemContratanteException e) {
            // FA2: mostra a mensagem e mantém o formulário preenchido
            exibirErro(e.getMessage());
        } catch (MenorDeIdadeException e) {
            // FA2: mostra a mensagem e mantém o formulário preenchido
            exibirErro(e.getMessage());
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }

    @FXML
    private void onRemoverClicado() {
        if (selecionado == null) {
            exibirErro("Selecione um cliente na tabela para remover.");
            return;
        }
        if (confirmar("Remover o cliente '" + selecionado.getNome() + "'?")) {
            try {
                clienteService.remover(selecionado.getId());
                exibirSucesso("Cliente removido com sucesso.");
                carregarLista();
                onNovoClicado();
            } catch (ClienteEmUsoException e) {
                // FA3
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

    /** Devolve null (após exibir o erro) se algum campo obrigatório estiver inválido. */
    private Cliente lerFormulario() {
        String nome = campoNome.getText() == null ? "" : campoNome.getText().trim();
        LocalDate data = campoDataNascimento.getValue();
        Contratante dono = comboContratante.getValue();

        if (nome.isEmpty()) {
            exibirErro("O campo nome está inválido.");
            return null;
        }
        if (data == null) {
            exibirErro("O campo data de nascimento está inválido.");
            return null;
        }
        if (dono == null) {
            exibirErro("Selecione o contratante dono do cliente.");
            return null;
        }

        Cliente c = new Cliente(nome, data, campoDescricaoFisica.getText(), campoEndereco.getText(), campoLocalDeTrabalho.getText(), dono);
        c.setContratante(dono);
        if (selecionado != null) {
            c.setId(selecionado.getId());
        }
        return c;
    }

    private void preencherFormulario(Cliente c) {
        campoNome.setText(c.getNome());
        campoDataNascimento.setValue(c.getDataNascimento());
        campoDescricaoFisica.setText(c.getDescricaoFisica());
        campoEndereco.setText(c.getEndereco());
        campoLocalDeTrabalho.setText(c.getLocalDeTrabalho());

        comboContratante.setValue(null);
        if (c.getContratante() != null) {
            for (Contratante item : comboContratante.getItems()) {
                if (item.getId() == c.getContratante().getId()) {
                    comboContratante.setValue(item);
                    break;
                }
            }
        }
    }

    private void limparFormulario() {
        campoNome.clear();
        campoDataNascimento.setValue(null);
        campoDescricaoFisica.clear();
        campoEndereco.clear();
        campoLocalDeTrabalho.clear();
        comboContratante.setValue(null);
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
