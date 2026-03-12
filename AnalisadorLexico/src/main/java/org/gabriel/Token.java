package org.gabriel;


public class Token {
    private String string;
    private int linha;
    private int coluna;


    public Token(String string, int linha, int coluna) {
        this.string = string;
        this.linha = linha;
        this.coluna = coluna;

    }


    public int getColuna() {
        return coluna;
    }

    public int getLinha() {
        return linha;
    }

    public String getString() {
        return string;
    }

    @Override
    public String toString() {
        return string +"\t" +
                linha +"\t" +
                coluna;
    }
}
