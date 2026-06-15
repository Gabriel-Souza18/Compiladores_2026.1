package org.gabriel;

public class Variavel {
    private final String nome;
    private final TipoVar tipo;
    private final Integer linha;
    private final Integer coluna;
    private final int nivel;
    private boolean inicializado;

    public Variavel(String nome, TipoVar tipo, Integer linha, Integer coluna, int nivel, boolean inicializado) {
        this.nome = nome;
        this.tipo = tipo;
        this.linha = linha;
        this.coluna = coluna;
        this.nivel = nivel;
        this.inicializado = inicializado;
    }

    public String nome()          { return nome; }
    public TipoVar tipo()         { return tipo; }
    public Integer linha()        { return linha; }
    public Integer coluna()       { return coluna; }
    public int nivel()            { return nivel; }
    public boolean inicializado() { return inicializado; }

    /** Marca a variável como inicializada (após atribuição). */
    public void setInicializado(boolean inicializado) {
        this.inicializado = inicializado;
    }

    @Override
    public String toString() {
        String indent = "  ".repeat(nivel);
        return indent + nome + " : " + tipo
                + " (linha " + linha + ", col " + coluna + ", nivel " + nivel + ")";
    }
}
