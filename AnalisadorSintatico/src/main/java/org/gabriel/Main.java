package org.gabriel;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Tokens tokens = new Tokens("src/main/java/org/gabriel/Entrada.txt");
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
