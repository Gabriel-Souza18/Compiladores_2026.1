package org.gabriel;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Tabela {

    // Pilha de escopos ativos: topo = escopo mais interno
    private final Deque<Map<String, Variavel>> pilhaEscopos = new ArrayDeque<>();

    // Acumula todas as variáveis conforme os escopos são fechados
    private final List<Variavel> historico = new ArrayList<>();

    /** Abre um novo escopo (chamado ao encontrar '{'). */
    public void abrirEscopo() {
        pilhaEscopos.push(new HashMap<>());
    }

    /**
     * Fecha o escopo atual (chamado ao encontrar '}').
     * As variáveis do escopo são acumuladas no histórico para impressão final.
     */
    public void fecharEscopo() {
        if (pilhaEscopos.isEmpty()) return;
        Map<String, Variavel> escopo = pilhaEscopos.pop();
        // Acumula em ordem de linha
        escopo.values().stream()
                .sorted((a, b) -> Integer.compare(a.linha(), b.linha()))
                .forEach(historico::add);
    }
    public int nivelAtual() {
        return pilhaEscopos.size() - 1;
    }

    public Variavel buscar(String nome) {
        for (Map<String, Variavel> escopo : pilhaEscopos) {
            Variavel v = escopo.get(nome);
            if (v != null) return v;
        }
        return null;
    }

    public boolean addNaTabela(Variavel variavel) {
        if (pilhaEscopos.isEmpty()) return false;
        Map<String, Variavel> escopoAtual = pilhaEscopos.peek();
        if (escopoAtual.containsKey(variavel.nome())) {
            return false;
        }
        escopoAtual.put(variavel.nome(), variavel);
        return true;
    }

    public void marcarInicializado(String nome) {
        for (Map<String, Variavel> escopo : pilhaEscopos) {
            Variavel v = escopo.get(nome);
            if (v != null) {
                v.setInicializado(true);
                return;
            }
        }
    }


    public void printTabela() {
        historico.stream()
                .sorted((a, b) -> {
                    if (a.nivel() != b.nivel()) return Integer.compare(a.nivel(), b.nivel());
                    return Integer.compare(a.linha(), b.linha());
                })
                .forEach(v -> IO.println(v.toString()));
    }
}
