package com.ultimarisada.model;


public class Avaliacao {
    private int id;
    private int valor;
    private String descricao;

    // Construtor
    public Avaliacao(int id, int valor, String descricao) {
        this.id = id;
        this.valor = valor;
        this.descricao = descricao;
    }

    public Avaliacao(int valor, String descricao) {
        this.valor = valor;
        this.descricao = descricao;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getValor() {
        return valor;
    }

    public String getDescricao() {
        return descricao;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}