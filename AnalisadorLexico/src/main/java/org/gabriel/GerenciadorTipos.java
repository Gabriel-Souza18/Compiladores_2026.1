package org.gabriel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GerenciadorTipos {
    private List<Tipo> tipos;

    public GerenciadorTipos() {
        tipos = new ArrayList<>();
        inicializarTipos();
    }

    private void inicializarTipos() {
        // SEPARADORES: caracteres únicos como ( ) { } [ ] ; ,
        tipos.add(new Tipo("SEPARADOR",
                "[\\(\\)\\{\\}\\[\\];,]",
                "[\\(\\)\\{\\}\\[\\];,]",
                Arrays.asList("NUMERO", "IDENTIFICADOR", "LITERAL", "SEPARADOR")));

        // NUMERO: sequência de dígitos
        tipos.add(new Tipo("NUMERO",
                "[0-9]",
                "[0-9]+(\\.[0-9]+)?",
                Arrays.asList("SEPARADOR", "OPERADOR_ARITMETICO", "OPERADOR_LOGICO")));

        // LITERAL: strings entre aspas duplas ou simples
        tipos.add(new Tipo("LITERAL",
                "[\"']",
                "[\"'][^\"']*[\"']",
                Arrays.asList("SEPARADOR", "OPERADOR_ARITMETICO", "OPERADOR_LOGICO")));

        // IDENTIFICADOR: começa com letra ou _ , seguido de letras, dígitos ou _
        tipos.add(new Tipo("IDENTIFICADOR",
                "[a-zA-Z_]",
                "[a-zA-Z_][a-zA-Z0-9_]*",
                Arrays.asList("SEPARADOR", "OPERADOR_ARITMETICO", "OPERADOR_LOGICO", "NUMERO")));

        // PALAVRA_RESERVADA: palavras reservadas
        tipos.add(new Tipo("PALAVRA_RESERVADA",
                "[a-zA-Z_]",
                "[a-zA-Z_][a-zA-Z0-9_]*",
                Arrays.asList("SEPARADOR", "OPERADOR_ARITMETICO", "OPERADOR_LOGICO")));

        // OPERADOR_ARITMETICO: + - * / % = ++ -- += -= *= /=
        tipos.add(new Tipo("OPERADOR_ARITMETICO",
                "[+\\-*/%=]",
                "[+\\-*/%=]+",
                Arrays.asList("NUMERO", "IDENTIFICADOR", "LITERAL", "SEPARADOR")));

        // OPERADOR_LOGICO: & | ! < > & | = && || != <= >=
        tipos.add(new Tipo("OPERADOR_LOGICO",
                "[&|!<>=]",
                "[&|!<>=]+",
                Arrays.asList("NUMERO", "IDENTIFICADOR", "LITERAL", "SEPARADOR")));
    }

    // Retorna o tipo que corresponde ao primeiro caractere
    public Tipo getTipoPorPrimeiro(char c) {
        for (Tipo tipo : tipos) {
            if (tipo.validaInicio(c)) {
                return tipo;
            }
        }
        return null;
    }

    // Retorna o tipo que corresponde completamente ao token
    public Tipo getTipoPorCompleto(String token) {
        for (Tipo tipo : tipos) {
            if (tipo.validaCompleto(token)) {
                return tipo;
            }
        }
        return null;
    }

    public boolean ehPalavraReservada(String token) {
        return token.equals("int") || token.equals("for") || token.equals("while") ||
               token.equals("if") || token.equals("else");
    }

    public String identificarTipo(String token) {
        if (ehPalavraReservada(token)) {
            return "PALAVRA_RESERVADA";
        }
        
        Tipo tipo = getTipoPorCompleto(token);
        if (tipo != null) {
            return tipo.getName();
        }
        
        return "ERRO";
    }
}

