package org.gabriel;


public class Token {
    private String string;
    private int linha;
    private int coluna;
    private String tipo;

    // Arrays para identificar tipos de tokens
    private static final char[] numeros = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final char[] operadoresLogicos = {'&', '|', '!', '<', '>', '='};
    private static final char[] operadoresAritmeticos = {'+', '-', '*', '/', '%'};
    private static final char[] separadores = {'(', ')', '{', '}', '[', ']', ';', ','};
    private static final String[] palavrasReservadas = {"int", "for"};

    public Token(String string, int linha, int coluna) {
        this.string = string;
        this.linha = linha;
        this.coluna = coluna;
        definirTipo();
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

    private void definirTipo(){
        if (string.isEmpty()) return;

        char caractere = string.charAt(0);

        //  numero
        if (contem(caractere, numeros)) {
            this.tipo = "Numero";
        }
        //  separador
        else if (contem(caractere, separadores)) {
            this.tipo = "Separador";
        }
        //  operador lógico
        else if (contem(caractere, operadoresLogicos)) {
            this.tipo = "Operador Logico";
        }
        //  operador aritmético
        else if (contem(caractere, operadoresAritmeticos)) {
            this.tipo = "Operador Aritmetico";
        }
        //  literal
        else if (caractere== '"' && this.string.charAt(this.string.length()-1)== '"') {
            this.tipo = "Literal";

        }
        // Se começa com letra ou underscore, é identificador
        else if (Character.isLetter(caractere) || caractere == '_') {
            this.tipo = "Identificador";
            if (contemPalavra(this.string,palavrasReservadas)){// se ta na lista é palavra reservada
                this.tipo= "Palavra Reservada";
            }
        }

    }

    private boolean contem(char c, char[] array) {
        for (char elemento : array) {
            if (c == elemento) return true;
        }
        return false;
    }

    private boolean contemPalavra(String palavra, String[] array) {
        for (String elemento : array) {
            if (palavra.equals(elemento)) return true;
        }
        return false;
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
