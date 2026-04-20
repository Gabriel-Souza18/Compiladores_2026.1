package org.gabriel;

import java.util.List;
import java.util.regex.Pattern;

public class Tipo {
    private final String name;
    private final List<String> separadoresPossiveis;
    private final Pattern patternInicio;
    private final Pattern patternCompleto;

    public Tipo(String name, String regexInicio, String regexCompleto, List<String> separadoresPossiveis) {
        this.name = name;
        this.separadoresPossiveis = separadoresPossiveis;
        this.patternInicio = Pattern.compile(regexInicio);
        this.patternCompleto = Pattern.compile(regexCompleto);
    }

    public String getName() {
        return name;
    }

    public List<String> getSeparadoresPossiveis() {
        return separadoresPossiveis;
    }

    // Verifica se o primeiro caractere corresponde ao regex inicial
    public boolean validaInicio(char c) {
        return patternInicio.matcher(String.valueOf(c)).matches();
    }

    // Verifica se a string completa corresponde ao regex completo
    public boolean validaCompleto(String token) {
        return patternCompleto.matcher(token).matches();
    }


    @Override
    public String toString() {
        return "Tipo{" +
                "name='" + name + '\'' +
                '}';
    }
}
