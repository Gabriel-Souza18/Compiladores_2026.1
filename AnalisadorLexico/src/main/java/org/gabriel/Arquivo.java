package org.gabriel;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class Arquivo {
    public static String ler(String caminho) throws IOException {
        BufferedReader buffRead = new BufferedReader(new java.io.FileReader(caminho));
        String linha = "";
        StringBuilder conteudo = new StringBuilder();
        while ((linha = buffRead.readLine()) != null) {
            conteudo.append(linha).append("\n");
        }
        buffRead.close();
    return conteudo.toString();
    }

    public static void escrever(String caminho, ArrayList<Token> tokens) throws IOException {
        java.io.FileWriter fileWrite = new java.io.FileWriter(caminho);
        fileWrite.write("Tokens\tlinha\tcoluna\ttipo\n");
        for (Token token : tokens) {
            fileWrite.write(String.format("%s\n", token.toString()));
        }

        fileWrite.close();
    }
}
