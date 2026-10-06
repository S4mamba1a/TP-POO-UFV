package main.java.com.ultimarisada.model;

import java.time.LocalDate;

public class Palhaco extends Pessoa{
    private String personalidade;
    private double cache;
    private double nota;
    private InstrumentoDeRisada[] instrumentos;
    //construtor
    public Palhaco(int id, String nome, LocalDate dataNascimento, String personalidade, double cache, double nota,
        InstrumentoDeRisada[] instrumentos) {
        super(id, nome, dataNascimento);
        this.personalidade = personalidade;
        this.cache = cache;
        this.nota = nota;
        this.instrumentos = instrumentos;
    }
    //getters
    public String getPersonalidade() {
        return personalidade;
    }
    public double getCache() {
        return cache;
    }
    public double getNota() {
        return nota;
    }
    public InstrumentoDeRisada[] getInstrumentos() {
        return instrumentos;
    } 
    //setters
    public void setPersonalidade(String personalidade) {
        this.personalidade = personalidade;
    }
    public void setCache(double cache) {
        this.cache = cache;
    }
    public void setNota(double nota) {
        this.nota = nota;
    }
    public void setInstrumentos(InstrumentoDeRisada[] instrumentos) {
        this.instrumentos = instrumentos;
    }
    //toString
    public void exibirResumo() {
        System.out.println("Nome: " + this.getNome() + " | Personalidade: " + this.personalidade + " | Nota: " + this.nota);
    }

    
}
