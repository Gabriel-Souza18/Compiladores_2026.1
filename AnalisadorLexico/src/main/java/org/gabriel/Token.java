package org.gabriel;


public class Token {
    private String string;
    private int linha;
    private int coluna;
    private String tipo;


    public Token(String string, int linha, int coluna,String tipo ) {
        this.string = string;
        this.linha = linha;
        this.coluna = coluna;
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return string +"\t" +
                linha +"\t" +
                coluna +"\t" +
                tipo;
    }

}
