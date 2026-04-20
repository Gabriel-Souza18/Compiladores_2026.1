package org.gabriel;

public class Token {
    private final String valor;
    private final int linha;
    private final int coluna;
    private final Tipo tipo;

    public Token(String valor, int linha, int coluna, Tipo tipo) {
        this.valor = valor;
        this.linha = linha;
        this.coluna = coluna;
        this.tipo = tipo;
    }

    public String getValor() {
        return valor;
    }

    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }

    public Tipo getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return this.valor + this.linha + this.coluna + this.tipo + "\n";
    }
}
