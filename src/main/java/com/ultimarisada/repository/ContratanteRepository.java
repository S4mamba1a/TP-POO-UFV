package com.ultimarisada.repository; 

import com.ultimarisada.model.Contratante;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ContratanteRepository {
    
    public void salvar(Contratante c) throws SQLException{
    }

    public void atualizar(Contratante c) throws SQLException{
    }

    public void remover(int id) throws SQLException{
    }

    public Contratante buscarPorId(int id) throws SQLException{
        return null;
    }

    public Contratante buscarPorNome(String nome) throws SQLException{
        return null;
    }

    public List<Contratante> listarTodos() throws SQLException{
        return null;
    }

    private Contratante montarContratante(ResultSet rs) throws SQLException{
        return null;
    }
}