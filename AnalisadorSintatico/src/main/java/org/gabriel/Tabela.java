package org.gabriel;

import java.util.ArrayList;
import java.util.List;

public class Tabela {
    private ArrayList<Variavel> tabelaVariaveis;

    public Tabela(){
        this.tabelaVariaveis = new ArrayList<>();
    }
    public String estaNaTabela (String nomeVar){
        for(Variavel var : tabelaVariaveis){
            if (var.nome().equals(nomeVar)){
                return  var.toString();
            }
        }
        return "False";
    }
    public Boolean addNaTabela(Variavel variavel){
        if(!estaNaTabela(variavel.nome()).equals("False")){
            return Boolean.FALSE;  // Variável já declarada
        }
        tabelaVariaveis.add(variavel);
        return Boolean.TRUE;
    }

    public void printTabela(){
        for(Variavel v : tabelaVariaveis){
            IO.println(v.toString());
        }
    }

}
