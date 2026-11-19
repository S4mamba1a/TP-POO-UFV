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

    public Contrato(int id, LocalDate dataFinal, StatusContrato status, Contratante contratante, Cliente cliente, Palhaco palhaco, InstrumentoDeRisada instrumento, Avaliacao avaliacao) {
        this.id = id;
        this.dataFinal = dataFinal;
        this.status = status;
        this.contratante = contratante;
        this.cliente = cliente;
        this.palhaco = palhaco;
        this.instrumento = instrumento;
        this.avaliacao = avaliacao;
    }

    public Contrato(LocalDate dataFinal, StatusContrato status, Contratante contratante, Cliente cliente, Palhaco palhaco, InstrumentoDeRisada instrumento) {
        this.id = 0;
        this.dataFinal = dataFinal;
        this.status = status;
        this.contratante = contratante;
        this.cliente = cliente;
        this.palhaco = palhaco;
        this.instrumento = instrumento;
        this.avaliacao = null;
    }

    public Contrato(){}

    // Getters
    public int getId() {
        return id;
    }

    public LocalDate getDataFinal() {
        return dataFinal;
    }

    public StatusContrato getStatus() {
        return status;
    }

    public Contratante getContratante() {
        return contratante;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Palhaco getPalhaco() {
        return palhaco;
    }

    public InstrumentoDeRisada getInstrumento() {
        return instrumento;
    }

    // pode ser null se o contrato ainda não foi avaliado
    public Avaliacao getAvaliacao() {
        return avaliacao;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setDataFinal(LocalDate dataFinal) {
        this.dataFinal = dataFinal;
    }

    public void setStatus(StatusContrato status) {
        this.status = status;
    }

    public void setContratante(Contratante contratante) {
        this.contratante = contratante;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setPalhaco(Palhaco palhaco) {
        this.palhaco = palhaco;
    }

    public void setInstrumento(InstrumentoDeRisada instrumento) {
        this.instrumento = instrumento;
    }

    public void setAvaliacao(Avaliacao avaliacao) {
        this.avaliacao = avaliacao;
    }
}