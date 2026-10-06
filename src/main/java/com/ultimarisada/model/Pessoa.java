package main.java.com.ultimarisada.model;

import java.time.LocalDate;

public abstract class Pessoa {
    private int id;
    private String nome;
    private LocalDate dataNascimento;

    //construtor
    public Pessoa(int id, String nome, LocalDate dataNascimento) {
        this.id = id;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
    }

    //getters
    public int getId() {
        return id;
    }
    public String getNome() {
        return nome;
    }
    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    //setters
    public void setId(int id) {
        this.id = id;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
    
    //toString
    public abstract String exibirResumo();
}
