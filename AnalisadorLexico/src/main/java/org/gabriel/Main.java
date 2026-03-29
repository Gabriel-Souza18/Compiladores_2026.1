package org.gabriel;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        String caminhoEntrada = "src/main/java/org/gabriel/codigoFonte.txt";
        String caminhoSaida = "src/main/java/org/gabriel/tokens.txt";

        int linha = 0;
        int coluna = 0;
        int i = 0; // posição do Pivo
        int j = 0; // pos do batedor

        GerenciadorTipos gerenciador = new GerenciadorTipos();

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
                   if (j + 1 < codigoFonte.length()) {
                       var prox = codigoFonte.charAt(j + 1);
                       if (prox == '/' ){
                           IO.println("COmentario Linha");
                           while (j < codigoFonte.length() && codigoFonte.charAt(j) != '\n') {
                               j++;
                           }
                           i=j;
                           continue;
                       }
                       if (prox == '*'){
                           IO.println("Comentario Bloco");
                           j += 2;
                           while (j < codigoFonte.length() - 1) {
                               if (codigoFonte.charAt(j) == '*' && codigoFonte.charAt(j + 1) == '/') {
                                   j += 2;
                                   break;
                               }
                               if (codigoFonte.charAt(j) == '\n') {
                                   linha++;
                                   coluna = 0;
                               } else {
                                   coluna++;
                               }
                               j++;
                           }
                           i=j;
                           continue;
                       }
                   }
               }

                j = i;
                StringBuilder tokenBuilder = new StringBuilder();

                char primeiroChar = codigoFonte.charAt(j);
                tokenBuilder.append(primeiroChar);
                j++;


                char batedor = codigoFonte.charAt(j);
                // Trata LITERAL especial
                if (primeiroChar == '"' ) {
                    while (true) {
                        if(j < codigoFonte.length()) {
                            batedor = codigoFonte.charAt(j);
                            tokenBuilder.append(batedor);
                            j++;
                            if (batedor == '"') {
                                break;
                            }
                        }else {
                            IO.println("LITERAL NAO FECHADO");
                            j=i;
                            tokenBuilder = new StringBuilder();
                            batedor = codigoFonte.charAt(j);
                            while(j<codigoFonte.length() && batedor != '\n'){
                                batedor = codigoFonte.charAt(j);
                                if(batedor!= '\n'){
                                    tokenBuilder.append(batedor);
                                }
                                j++;
                            }
                            linha++;
                            coluna=0;
                            i=j;
                            break;
                        }
                    }
                } else if(primeiroChar == '\'') {
                    batedor = codigoFonte.charAt(j);
                    tokenBuilder.append(batedor);
                    j++;
                    if (j < codigoFonte.length() && codigoFonte.charAt(j) == '\'') {
                        tokenBuilder.append('\'');
                        j++;
                    } else {
                        IO.println("ERRO: Char não fechado corretamente");
                    }
                    //SEM BREAK AQUI
                }
                else{
                    // OUTROS TIPOS
                    Tipo tipoAtual = gerenciador.getTipoPorPrimeiro(primeiroChar);


                    boolean jaFoiPonto = false;
                    if(tipoAtual!= null && !tipoAtual.getName().equals("SEPARADOR")){
                        while (j < codigoFonte.length()) {
                            batedor = codigoFonte.charAt(j);


                            // Para se encontrar espaço ou quebra de linha
                            if (batedor == ' ' || batedor == '\n' || batedor == '\t') {
                                break;
                            }

                            String tokenAtual = tokenBuilder.toString();
                            Tipo tipoProximo = gerenciador.getTipoPorPrimeiro(batedor);

                            // verificação do numero real
                            if (tipoAtual.getName().equals("NUMERO") && batedor == '.' && !jaFoiPonto) {
                                tokenBuilder.append(batedor);
                                j++;
                                jaFoiPonto = true;
                                continue;  // Continua lendo
                            }
                            if (tipoProximo != null) {
                                if (tipoAtual.getName().equals(tipoProximo.getName())) {
                                    tokenBuilder.append(batedor);
                                    j++;
                                    continue;
                                }
                                else if (tipoAtual.getSeparadoresPossiveis().contains(tipoProximo.getName())) {
                                    break;// so break sem append
                                }
                            }
                            // Trata casos especiais de operadores compostos
                            if (temOperadorComposto(tokenAtual, batedor)) {
                                tokenBuilder.append(batedor);
                                j++;
                                break;
                            }
                            if (tipoProximo != null && !tipoAtual.getSeparadoresPossiveis().contains(tipoProximo.getName())) {
                                tokenBuilder.append(batedor);
                                j++;
                                break;
                            }
                            break;
                        }
                    }
                }

                String token = tokenBuilder.toString();
                String tipo = gerenciador.identificarTipo(token);
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

    // Valida se pode formar operador composto
    private static boolean temOperadorComposto(String token, char prox) {
        return (token.equals("+") && (prox == '+' || prox == '=')) ||
               (token.equals("-") && (prox == '-' || prox == '=')) ||
               (token.equals("*") && prox == '=') ||
               (token.equals("/") && prox == '=') ||
               (token.equals("%") && prox == '=') ||
               ((token.equals("<") || token.equals(">") || token.equals("!") || token.equals("=")) && prox == '=') ||
               (token.equals("&") && prox == '&') ||
               (token.equals("|") && prox == '|');
    }
}
