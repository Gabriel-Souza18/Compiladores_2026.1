package org.gabriel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Tokens {
    private List<Token> tokens;
    private Token tokenAtual;
    private int indexAtual;

    public Tokens(String caminhoEntrada) {
        this.tokens =LerEntrada(caminhoEntrada) ;
        this.indexAtual =0;
        this.tokenAtual = tokens.get(indexAtual);
    }

    private List<Token> LerEntrada(String caminhoEntrada) {
        List<Token> resultado = new ArrayList<>();
        Path caminho = Path.of(caminhoEntrada);

        try {
            List<String> linhas = Files.readAllLines(caminho, StandardCharsets.UTF_8);

            for (int i = 0; i < linhas.size(); i++) {
                String linha = linhas.get(i);
                if (i == 0 || linha.isBlank()) {
                    continue;
                }

                String[] partes = linha.split("\\t");
                if (partes.length < 4) {
                    throw new IllegalArgumentException("Linha invalida no arquivo de entrada: " + linha);
                }

                String valor = partes[0];
                int linhaToken = Integer.parseInt(partes[1]);
                int colunaToken = Integer.parseInt(partes[2]);
                Tipo tipo = Tipo.valueOf(partes[3]);

                resultado.add(new Token(valor, linhaToken, colunaToken, tipo));
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler arquivo de entrada: " + caminhoEntrada, e);
        }

        return resultado;
    }

    public void lerProx(){
        indexAtual ++;
        tokenAtual = tokens.get(indexAtual);
    }

    public Token getTokenAtual() {
        return tokenAtual;
    }
    public void  printTokens(){
        for  (Token token : tokens) {
            IO.print(token.toString());
        }
    }

}
