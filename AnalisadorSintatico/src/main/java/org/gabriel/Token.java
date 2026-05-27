package org.gabriel;

public record Token(String valor, int linha, int coluna, Tipo tipo) {

    @Override
    public String toString() {
        return this.valor + this.linha + this.coluna + this.tipo + "\n";
    }
}
