package org.gabriel;

import java.util.ArrayList;

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
                    coluna = 0;
                    i++;
                    j = i;
                    continue;
                }

                if (pivo == ' ' || pivo == '\t') {
                    i++;
                    coluna++;
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
                       // IO.println("Achou comentario de Linha");
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

                j = i;
                StringBuilder newString = new StringBuilder();
                newString.append(codigoFonte.charAt(j));
                j++;


                while (j < codigoFonte.length()) {

                    char proxChar = codigoFonte.charAt(j);

                    // Para se encontrar espaço ou quebra de linha
                    if (proxChar == ' ' || proxChar == '\n' || proxChar == '\t') {
                        break;
                    }

                    String tokenAtual = newString.toString();
                    String tipoAtual = identificarTipo(tokenAtual);
                    String tipoProx = identificarTipo(String.valueOf(proxChar));

                    //para se o tipo mudar
                    if (!tipoAtual.equals(tipoProx)) {
                        // FALTA ESSES AQUI O ++, --, ==, !=, >=, <=
                        if ((tokenAtual.equals("+") || tokenAtual.equals("-") || tokenAtual.equals("=") ||
                             tokenAtual.equals("<") || tokenAtual.equals(">") || tokenAtual.equals("!") ||
                             tokenAtual.equals("&") || tokenAtual.equals("|")) &&
                            (proxChar == '=' || proxChar == '+' || proxChar == '-' || proxChar == '&' || proxChar == '|')) {
                            newString.append(proxChar);
                            j++;
                        } else {
                            break;
                        }
                    } else {
                        newString.append(proxChar);
                        j++;
                    }
                }

                String token = newString.toString();
                String tipo = identificarTipo(token);
                Token newToken = new Token(token, linha, coluna, tipo);
                tokens.add(newToken);
                IO.println("Token: " + token + " | Tipo: " + tipo);

                i = j;
                coluna += token.length();
            }

            Arquivo.escrever(caminhoSaida, tokens);
        } catch (Exception e) {
            IO.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }

    public static String identificarTipo(String token) {
        // Palavras reservadas
        if (token.equals("int") || token.equals("for")) {
            return "PALAVRA_RESERVADA";
        }

        // Separadores
        if (token.length() == 1) {
            char c = token.charAt(0);
            if (c == '(' || c == ')' || c == '{' || c == '}' || c == '[' || c == ']' || c == ';' || c == ',') {
                return "SEPARADOR";
            }
        }

        // Números
        if (apenasNumeros(token)) {
            return "NUMERO";
        }

        // Operadores aritméticos (simples e compostos)
        if (ehOperadorAritmetico(token)) {
            return "OPERADOR_ARITMETICO";
        }

        // Operadores lógicos (simples e compostos)
        if (ehOperadorLogico(token)) {
            return "OPERADOR_LOGICO";
        }

        // Literais (strings ou caracteres)
        if ((token.startsWith("\"") && token.endsWith("\"")) ||
            (token.startsWith("'") && token.endsWith("'"))) {
            return "LITERAL";
        }

        // Padrão: identificador
        return "IDENTIFICADOR";
    }

    public static boolean apenasNumeros(String token) {
        for (char c : token.toCharArray()) {
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return !token.isEmpty();
    }

    public static boolean ehOperadorAritmetico(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") ||
               token.equals("/") || token.equals("%") || token.equals("=") ||
               token.equals("++") || token.equals("--") || token.equals("+=") ||
               token.equals("-=") || token.equals("*=") || token.equals("/=");
    }

    public static boolean ehOperadorLogico(String token) {
        return token.equals("&") || token.equals("|") || token.equals("!=") ||
               token.equals("<") || token.equals(">") || token.equals("==") ||
               token.equals("<=") || token.equals(">=") || token.equals("&&") ||
               token.equals("||") || token.equals("!");
    }
}
