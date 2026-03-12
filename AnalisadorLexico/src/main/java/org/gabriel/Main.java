package org.gabriel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String caminhoEntrada = "src/main/java/org/gabriel/codigoFonte.txt";
        String caminhoSaida = "src/main/java/org/gabriel/tokens.txt";

        int linha = 0;
        int coluna = 0;
        int i = 0;
        int j = 0;

        try {
            String codigoFonte = Arquivo.ler(caminhoEntrada);
            ArrayList<Token> tokens = new ArrayList<>();

            while (i < codigoFonte.length()) {
                char pivo = codigoFonte.charAt(i);

                if (pivo == '\n') {
                    linha++;
                    coluna=0;
                    i++;
                    j = i;
                    continue;
                }

                if (pivo == ' ') {
                    i++;
                    j = i;
                    continue;
                }

                // avança o batedor até encontrar espaço, \n ou fim
                char batedor = codigoFonte.charAt(j);
                while (j < codigoFonte.length() && batedor != ' ' && batedor != '\n') {
                    j++;
                    if (j < codigoFonte.length()) {
                        batedor = codigoFonte.charAt(j);
                    }
                }

                // captura o token entre pivo (i) e batedor (j)

                StringBuilder newToken = new StringBuilder();
                for (; i < j; i++) {
                    newToken.append(codigoFonte.charAt(i));
                }


                tokens.add(new Token(newToken.toString(), linha, coluna));
                coluna += newToken.length()+1;
            }

            Arquivo.escrever(caminhoSaida, tokens);
        } catch (Exception e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }

    }
}
