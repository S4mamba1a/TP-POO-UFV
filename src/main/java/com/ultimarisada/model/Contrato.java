package com.ultimarisada.model;

import java.time.LocalDate;

public class Contrato {
    private int id;
    private LocalDate dataFinal;
    private StatusContrato status;
    private Contratante contratante;
    private Cliente cliente;
    private Palhaco palhaco;
    private InstrumentoDeRisada instrumento;
    private Avaliacao avaliacao;

    // Getters
    public int getId() {
        return 0;
    }

    public LocalDate getDataFinal() {
        return null;
    }

    public StatusContrato getStatus() {
        return null;
    }

    public Contratante getContratante() {
        return null;
    }

    public Cliente getCliente() {
        return null;
    }

    public Palhaco getPalhaco() {
        return null;
    }

    public InstrumentoDeRisada getInstrumento() {
        return null;
    }

    public Avaliacao getAvaliacao() {
        return null;
    }

    // Setters
    public void setId(int id) {
    }

    public void setDataFinal(LocalDate dataFinal) {
    }

    public void setStatus(StatusContrato status) {
    }

    public void setContratante(Contratante contratante) {
    }

    public void setCliente(Cliente cliente) {
    }

    public void setPalhaco(Palhaco palhaco) {
    }

    public void setInstrumento(InstrumentoDeRisada instrumento) {
    }

    public void setAvaliacao(Avaliacao avaliacao) {
    }
}
