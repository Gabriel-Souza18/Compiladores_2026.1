package org.gabriel;

public record Variavel(String nome, TipoVar tipo, Integer linha, Integer coluna, int nivel) {

    @Override
    public String toString() {
        String indent = "  ".repeat(nivel);
        return indent + nome + " : " + tipo
                + " (linha " + linha + ", col " + coluna + ", nivel " + nivel + ")";
    }
}
