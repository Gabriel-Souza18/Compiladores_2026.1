package org.gabriel;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        String caminhoEntrada = "src/main/java/org/gabriel/codigoFonte.txt";
        String caminhoSaida = "src/main/java/org/gabriel/tokens.txt";

        int linha = 0;
        int coluna = 0;
        int i = 0; // posiçaão do PIvo
        int j = 0; // pos do batedor

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
                           var anterior = codigoFonte.charAt(j-1);
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
                StringBuilder tokenBuilder = new StringBuilder();
                tokenBuilder.append(codigoFonte.charAt(j));
                j++;

                while (j < codigoFonte.length()) {
                    char batedor = codigoFonte.charAt(j);

                    // Para se encontrar espaço ou quebra de linha
                    if (batedor == ' ' || batedor == '\n' || batedor == '\t') {
                        break;
                    }

                    String tokenAtual = tokenBuilder.toString();
                    String tipoAtual = identificarTipo(tokenAtual);
                    String tipoProx = identificarTipo(String.valueOf(batedor));

                    // Para se o tipo mudar
                    if (!tipoAtual.equals(tipoProx)) {
                        if ((tokenAtual.equals("+") &&(batedor == '+'|| batedor =='=' ))){
                            tokenBuilder.append(batedor);
                            j++;
                            break;
                        }else if (tokenAtual.equals("-") && (batedor == '-'||batedor == '=') ){
                            tokenBuilder.append(batedor);
                            j++;
                            break;
                        } else if ((tokenAtual.equals("<")|| tokenAtual.equals(">")||
                                    tokenAtual.equals("!")|| tokenAtual.equals("="))
                                    && batedor == '=') {
                            tokenBuilder.append(batedor);
                            j++;
                            break;
                        } else if (tokenAtual.equals("&") && batedor =='&') {
                            tokenBuilder.append(batedor);
                            j++;
                            break;
                        } else if (tokenAtual.equals("|")&& batedor =='|') {
                            tokenBuilder.append(batedor);
                            j++;
                            break;
                        } else {
                            break;
                        }
                    }else {
                        tokenBuilder.append(batedor);
                        j++;
                    }
                }

                String token = tokenBuilder.toString();
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
       if (token.equals("int") || token.equals("for")) { // adicionar mais palavras reservadas
           return "PALAVRA_RESERVADA";
       }
       if (token.length() == 1) {
           char c = token.charAt(0);
           if (c == '(' || c == ')' || c == '{' || c == '}' || c == '[' || c == ']' || c == ';' || c == ',') {
               return "SEPARADOR";
           }
       }
       if (token.matches("[0-9]*")) {
           return "NUMERO";
       }
       if (ehOperadorAritmetico(token)) {
           return "OPERADOR_ARITMETICO";
       }
       if (ehOperadorLogico(token)) {
           return "OPERADOR_LOGICO";
       }
       if ((token.startsWith("\"") && token.endsWith("\"")) ||
               (token.startsWith("'") && token.endsWith("'"))) {
           return "LITERAL";
       }
       if (token.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
           return "IDENTIFICADOR";
       }
       else {
           return "ERRO";
       }
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
