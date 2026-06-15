package org.gabriel;

public class Main {
    public static void main(String[] args) {
        String arquivo = args.length > 0
                ? args[0]
                : "src/main/java/org/gabriel/Entrada.txt";

        Tokens tokens = new Tokens(arquivo);
        Gramatica gramatica = new Gramatica(tokens);
        try {
            gramatica.programa();
        } catch (Exception e) {
            IO.println(e.getMessage());
            gramatica.incrementarErros();
        }

        if (gramatica.getErros() == 0) {
            IO.println("VALIDO");
        } else {
            IO.println("INVALIDO");
        }
    }
}
