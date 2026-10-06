package main.java.com.ultimarisada.model;

public class InstrumentoDeRisada {
    private int id;
    private double valor;
    private String nome;
    private String descricao;

    //construtor
    public InstrumentoDeRisada(int id, double valor, String nome, String descricao) {
        this.id = id;
        this.valor = valor;
        this.nome = nome;
        this.descricao = descricao;
    }

    //getters
    public int getId() {
        return id;
    }
    public double getValor() {
        return valor;
    }
    public String getNome() {
        return nome;
    }
    public String getDescricao() {
        return descricao;
    }
    
    //setters
    public void setId(int id) {
        this.id = id;
    }
    public void setValor(double valor) {
        this.valor = valor;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
