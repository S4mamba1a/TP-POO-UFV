package main.java.com.ultimarisada.repository;

import main.java.com.ultimarisada.model.InstrumentoDeRisada;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;



public class InstrumentoRepository {
    
    public void salvar(InstrumentoDeRisada i, int idPalhacoDono){

    }

    public void atualizar(InstrumentoDeRisada i){

    }

    public void remover(int id){

    }

    public InstrumentoDeRisada buscarPorId(int id){
        return null;
    }

    public List<InstrumentoDeRisada> listarPorPalhaco(int idPalhaco){
        return new ArrayList<>();
    }

    private InstrumentoDeRisada montarInstrumento(ResultSet rs) throws SQLException {
        return null;
    }
}
