package org.gabriel;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        Tokens tokens = new Tokens("src/main/java/org/gabriel/Entrada.txt");
        Gramatica gramatica = new Gramatica(tokens);
        try {
            gramatica.programa();
            IO.println("VALIDO");

        }catch (Exception e){
            IO.println(e.getMessage());
        }
    }

}
