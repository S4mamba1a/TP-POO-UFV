package main.java.com.ultimarisada.model;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class Palhaco extends Pessoa{
    private String personalidade;
    private double cache;
    private double nota;
    private List<InstrumentoDeRisada> instrumentos;

    //construtor
    public Palhaco(int id, String nome, LocalDate dataNascimento, String personalidade, double cache, double nota) {
        super(id, nome, dataNascimento);
        this.personalidade = personalidade;
        this.cache = cache;
        this.nota = nota;
        this.instrumentos = new ArrayList<>();
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
    public List<InstrumentoDeRisada> getInstrumentos() {
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
    public void setInstrumentos(List<InstrumentoDeRisada> instrumentos) {
        this.instrumentos = instrumentos;
    }

    //toString
    @Override
    public String exibirResumo() {
        return String.format("Nome: " + this.getNome() + " | Personalidade: " + this.personalidade + " | Nota: " + this.nota);
    }
}
