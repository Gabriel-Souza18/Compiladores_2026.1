package org.gabriel;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        Tokens tokens = new Tokens("src/main/java/org/gabriel/Entrada.txt");

        tokens.printTokens();
    }
}
