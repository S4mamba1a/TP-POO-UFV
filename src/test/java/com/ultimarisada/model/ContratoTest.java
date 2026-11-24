package com.ultimarisada.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ContratoTest {
    LocalDate dataFinal;
    StatusContrato status;
    Contratante contratante;
    Cliente cliente;
    Palhaco palhaco;
    InstrumentoDeRisada ins;
    Avaliacao avaliacao;
    Contrato contrato;

    @BeforeEach 
    void configurarVariaveis(){
        dataFinal = LocalDate.now();
        status = StatusContrato.PENDENTE;
        contratante = new Contratante("José", LocalDate.now(), "1234");
        cliente = new Cliente("Joaquim", LocalDate.now(), "Homem alto de pele clara", "Rua Florestal, número 123", "Mercado", contratante);
        palhaco = new Palhaco("Penny", LocalDate.now(), "Esquentadinho", 1890);
        ins = new InstrumentoDeRisada(300, "Espada de Balão", "Corta até vento!");
        avaliacao = new Avaliacao(5, "Serviço muito bom!");
        contrato = new Contrato(10, dataFinal, status, contratante, cliente, palhaco, ins, avaliacao);
    }

    @Test
    @Order(1)
    void testGetAvaliacao() {
        assertEquals(avaliacao.getId(), contrato.getAvaliacao().getId());
    }

    @Test
    @Order(2)
    void testGetCliente() {
        assertEquals(cliente.getId(), contrato.getCliente().getId());
    }

    @Test
    @Order(3)
    void testGetContratante() {
        assertEquals(contratante.getId(), contrato.getContratante().getId());
    }

    @Test
    @Order(4)
    void testGetDataFinal() {
        assertEquals(dataFinal.toString(), contrato.getDataFinal().toString());
    }

    @Test
    @Order(5)
    void testGetId() {
        assertEquals(10, contrato.getId());
    }

    @Test
    @Order(6)
    void testGetInstrumento() {
        assertEquals(ins.getId(), contrato.getInstrumento().getId());
    }

    @Test
    @Order(7)
    void testGetPalhaco() {
        assertEquals(palhaco.getId(), contrato.getPalhaco().getId());
    }

    @Test
    @Order(8)
    void testGetStatus() {
        assertEquals(status, contrato.getStatus());
    }

    @Test
    @Order(9)
    void testSetAvaliacao() {
        Avaliacao av = new Avaliacao(3, "Serviço poderia ter sido mais limpo.");
        contrato.setAvaliacao(av);
        assertEquals(av, contrato.getAvaliacao());
    }

    @Test
    @Order(10)
    void testSetCliente() {
        Cliente c = new Cliente("Samuel", dataFinal, "Homem alto de cabelo cacheado", "Rua Pipipipopopo, número 67", "Marte", contratante);
        contrato.setCliente(c);
        assertEquals(c, contrato.getCliente());
    }

    @Test
    @Order(11)
    void testSetContratante() {
        Contratante ct = new Contratante("Almiro", LocalDate.now(), "1234");
        contrato.setContratante(ct);
        assertEquals(ct, contrato.getContratante());
    }

    @Test
    @Order(12)
    void testSetDataFinal() {
        LocalDate l = LocalDate.of(2006, 12, 1);
        contrato.setDataFinal(l);
        assertEquals(l, contrato.getDataFinal());
    }

    @Test
    @Order(13)
    void testSetId() {
        int i = 100;
        contrato.setId(i);
        assertEquals(i, contrato.getId());
    }

    @Test
    @Order(14)
    void testSetInstrumento() {
        InstrumentoDeRisada in = new InstrumentoDeRisada(700, "Buzina de Carro em Alta Velocidade", "Te mata de rir com tanto barulho!");
        contrato.setInstrumento(in);
        assertEquals(in, contrato.getInstrumento());
    }

    @Test
    @Order(15)
    void testSetPalhaco() {
        Palhaco p = new Palhaco("Robertinho", LocalDate.now(), "Explosivo", 9000);
        contrato.setPalhaco(p);
        assertEquals(p, contrato.getPalhaco());
    }

    @Test
    @Order(16)
    void testSetStatus() {
        StatusContrato s = StatusContrato.CANCELADO; 
        contrato.setStatus(s);
        assertEquals(s, contrato.getStatus());
    }
}
