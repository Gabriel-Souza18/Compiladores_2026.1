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

               if (pivo == '/'){ // achar comentario
                   var prox = codigoFonte.charAt(i+1);
                   if (prox== '/' ){
                       char batedor = codigoFonte.charAt(j);
                       while (batedor != '\n'){
                           j++;
                           i++;
                           if (j < codigoFonte.length()) {
                               batedor = codigoFonte.charAt(j);
                           }
                       }
                       IO.println("Achou comentario de Linha");
                       continue;
                   }
                   if (prox == '*'){
                       j++;
                       char batedor = codigoFonte.charAt(j);
                       while (true){
                           j++;
                           i++;
                           if (j < codigoFonte.length()) {
                               batedor = codigoFonte.charAt(j);
                               coluna ++;
                           }
                           if (batedor == '\n'){
                               linha++;
                               coluna =0;
                           }
                           var anterior = codigoFonte.charAt(j-1); // ISSO VAI DAR MERDA!!!
                           if (anterior == '*' && batedor == '/'){
                               i+=2;
                               j=i;
                               coluna+=2;
                               IO.println("Achou comentario de bloco");
                               break;
                           }
                       }
                       continue;
                   }

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

                StringBuilder newString = new StringBuilder();
                for (; i < j; i++) {
                    newString.append(codigoFonte.charAt(i));
                }

                Token newToken = new Token(newString.toString(), linha, coluna);
                tokens.add(newToken);
                IO.println("Novo Token");

                coluna += newString.length()+1;
            }

            Arquivo.escrever(caminhoSaida, tokens);
        } catch (Exception e) {
            IO.println("Erro ao ler o arquivo: " + e.getMessage());
        }

    }
}
