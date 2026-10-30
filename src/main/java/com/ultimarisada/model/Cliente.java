package com.ultimarisada.model;

import java.time.LocalDate;

public class Cliente extends Pessoa {
    private String descricaoFisica;
    private String endereco;
    private String localDeTrabalho;
    private Contratante contratante;

    public Cliente(int id, String nome, LocalDate dataNascimento, String descricaoFisica,
            String endereco, String localDeTrabalho, Contratante contratante) {
        super(id, nome, dataNascimento);
        this.descricaoFisica = descricaoFisica;
        this.endereco = endereco;
        this.localDeTrabalho = localDeTrabalho;
        this.contratante = contratante;
    }

    public Cliente(String nome, LocalDate dataNascimento, String descricaoFisica,
            String endereco, String localDeTrabalho, Contratante contratante) {
        super(nome, dataNascimento);
        this.descricaoFisica = descricaoFisica;
        this.endereco = endereco;
        this.localDeTrabalho = localDeTrabalho;
        this.contratante = contratante;
    }

    public String getDescricaoFisica() {
        return descricaoFisica;
    }

    public void setDescricaoFisica(String descricaoFisica) {
        this.descricaoFisica = descricaoFisica;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getLocalDeTrabalho() {
        return localDeTrabalho;
    }

    public void setLocalDeTrabalho(String localDeTrabalho) {
        this.localDeTrabalho = localDeTrabalho;
    }

    public Contratante getContratante() {
        return contratante;
    }

    public void setContratante(Contratante contratante) {
        this.contratante = contratante;
    }

    @Override
    public String exibirResumo() {
        return getNome() + " - " + getDescricaoFisica();
    }
}
