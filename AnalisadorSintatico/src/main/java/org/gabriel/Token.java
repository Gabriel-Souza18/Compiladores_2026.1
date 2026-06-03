package org.gabriel;

public record Token(String valor, int linha, int coluna, TipoToken tipoToken) {

    @Override
    public String toString() {
        return this.valor + this.linha + this.coluna + this.tipoToken + "\n";
    }
}
