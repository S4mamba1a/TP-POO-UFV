package com.ultimarisada.view.contratante;

import com.ultimarisada.model.Contratante;

public class SessaoContratante {
    private Contratante contratanteLogado;

    public SessaoContratante(){
        contratanteLogado = null;
    }
    public void login(Contratante c){
        contratanteLogado = new Contratante(c);
    }
    public void logout(){
        contratanteLogado = null;
    }
    public Contratante getContratanteLogado(){
        return contratanteLogado;
    }
    public boolean estaLogado(){
        if(contratanteLogado != null) return true;
        return false;
    }
}
