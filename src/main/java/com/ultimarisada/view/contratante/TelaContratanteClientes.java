package com.ultimarisada.view.contratante;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

import com.ultimarisada.exceptions.ClienteJaCadastradoException;
import com.ultimarisada.exceptions.ClienteSemContratanteException;
import com.ultimarisada.model.Cliente;
import com.ultimarisada.service.ClienteService;
import com.ultimarisada.service.ContratanteService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TelaContratanteClientes {
    private Stage stage;
    private ClienteService clienteService;
    private SessaoContratante sessao;

    //campos de texto e data
    @FXML private TextField campoNome;
    @FXML private DatePicker campoDataNascimento;
    @FXML private TextField campoDescricaoFisica;
    @FXML private TextField campoEndereco;
    @FXML private TextField campoLocalDeTrabalho;
    //tabela
    @FXML private TableView<Cliente> tabelaClientes;
    @FXML private TableColumn<Cliente, Integer> colunaId;
    @FXML private TableColumn<Cliente, String> colunaNome;
    @FXML private TableColumn<Cliente, LocalDate> colunaDataNascimento;
    @FXML private TableColumn<Cliente, String> colunaDescricaoFisica;
    @FXML private TableColumn<Cliente, String> colunaEndereco;
    @FXML private TableColumn<Cliente, String> colunaLocalDeTrabalho;

    public TelaContratanteClientes(Stage stage, SessaoContratante sessao) {
        this.stage = stage;
        this.sessao = sessao;
        this.clienteService = new ClienteService();
    }

    public void exibir() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/contratante/TelaContratanteClientes.fxml"));
            loader.setController(this);
            Parent raiz = loader.load();
            stage.setScene(new Scene(raiz));
            stage.setTitle("Contratante — Meus Clientes");
            stage.sizeToScene();
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao carregar /view/contratante/TelaContratanteClientes.fxml", e);
        }
    }
    //inicializa os getters
    @FXML private void initialize() {
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaDataNascimento.setCellValueFactory(new PropertyValueFactory<>("dataNascimento"));
        colunaDescricaoFisica.setCellValueFactory(new PropertyValueFactory<>("descricaoFisica"));
        colunaEndereco.setCellValueFactory(new PropertyValueFactory<>("endereco"));
        colunaLocalDeTrabalho.setCellValueFactory(new PropertyValueFactory<>("localDeTrabalho"));

        carregarMeusClientes();
    }
    //coloca os valores do cliente do contratante
    private void carregarMeusClientes() {
        try {
            int idContratante = sessao.getContratanteLogado().getId();
            tabelaClientes.getItems().setAll(clienteService.listarPorContratante(idContratante));
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }
    // le as informacoes escritas, cadastra como um cliente, carrega a pagina atualizada e depois ele limpa o que voce escreveu
    @FXML private void onCadastrarClicado() {
        Cliente c = lerFormulario();
        if (c == null){ return; }

        try {
            clienteService.cadastrar(c);
            exibirSucesso("Cliente cadastrado com sucesso.");
            carregarMeusClientes();
            limparFormulario();
        } catch (ClienteJaCadastradoException e) {
            exibirErro(e.getMessage());
        } catch (ClienteSemContratanteException e) {
            exibirErro(e.getMessage());
        } catch (SQLException e) {
            exibirErro("Erro ao acessar o banco de dados. Tente novamente.");
        }
    }
    //chama limpa
    @FXML private void onLimparClicado() {
            limparFormulario();
        }
        //coloca como cliente os valores escritos
    private Cliente lerFormulario() {
        String nome = campoNome.getText().trim();
        LocalDate data = campoDataNascimento.getValue();

        if (nome.isEmpty()) {
            exibirErro("O campo nome está inválido.");
            return null;
        }
        if (data == null) {
            exibirErro("O campo data de nascimento está inválido.");
            return null;
        }

        Cliente c = new Cliente();
        c.setNome(nome);
        c.setDataNascimento(data);
        c.setDescricaoFisica(campoDescricaoFisica.getText());
        c.setEndereco(campoEndereco.getText());
        c.setLocalDeTrabalho(campoLocalDeTrabalho.getText());
        c.setContratante(sessao.getContratanteLogado());
        return c;
    }
    //volta para o menu
    @FXML private void onVoltarClicado() {
        new TelaContratanteMenu(stage, sessao).exibir(); // ainda nao tem o menu
    }
    //limpa
    private void limparFormulario() {
        campoNome.clear();
        campoDataNascimento.setValue(null);
        campoDescricaoFisica.clear();
        campoEndereco.clear();
        campoLocalDeTrabalho.clear();
    }
    //mensagens-------------------------------------------------------------
    private void exibirErro(String mensagem) {
        new Alert(Alert.AlertType.ERROR, mensagem).showAndWait();
    }

    private void exibirSucesso(String mensagem) {
        new Alert(Alert.AlertType.INFORMATION, mensagem).showAndWait();
    }
}
