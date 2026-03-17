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
    /*tipos:
    Identificador
    Palavra Reservada
    Numero
    Literal
    Comentario
    Operadores Logicos e aritmeticos
    Separador
    */


    public int getColuna() {
        return coluna;
    }

    public int getLinha() {
        return linha;
    }

    public String getString() {
        return string;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
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
