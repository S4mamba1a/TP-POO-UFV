package main.java.com.ultimarisada.model;

import java.time.LocalDate;

public class Contratante extends Pessoa {
    private String senha;

    // Construtor
    public Contratante(int id, String nome, LocalDate dataNascimento, String senha) {
        super(id, nome, dataNascimento);
        this.senha = senha;
    }

    public Contratante(String nome, LocalDate dataNascimento, String senha) {
        super(nome, dataNascimento);
        this.senha = senha;
    }

    // Getters
    public String getSenha() {
        return senha;
    }

    // Setters
    public void setSenha(String senha) {
        this.senha = senha;
    }

    //toString
    @Override
    public String exibirResumo() {
        return getNome();
    }
}