package org.gabriel;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tabela de símbolos com suporte a escopos aninhados.
 *
 * Cada '{' abre um novo escopo (abrirEscopo) e cada '}' o fecha (fecharEscopo).
 * A busca percorre a pilha do escopo mais interno para o mais externo,
 * permitindo shadowing entre escopos distintos.
 * A impressão é feita apenas no final, via printTabela().
 */
public class Tabela {

    // Pilha de escopos ativos: topo = escopo mais interno
    private final Deque<Map<String, Variavel>> pilhaEscopos = new ArrayDeque<>();

    // Acumula todas as variáveis conforme os escopos são fechados
    private final List<Variavel> historico = new ArrayList<>();

    // -------------------------------------------------------
    // Gestão de escopos
    // -------------------------------------------------------

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

    /** Retorna o nível do escopo atual (0 = global, 1 = primeiro aninhado, ...). */
    public int nivelAtual() {
        return pilhaEscopos.size() - 1;
    }

    // -------------------------------------------------------
    // Operações sobre variáveis
    // -------------------------------------------------------

    /**
     * Busca uma variável do escopo mais interno para o mais externo.
     * @return a Variavel encontrada, ou {@code null} se não existir em nenhum escopo visível.
     */
    public Variavel buscar(String nome) {
        for (Map<String, Variavel> escopo : pilhaEscopos) {
            Variavel v = escopo.get(nome);
            if (v != null) return v;
        }
        return null;
    }

    /**
     * Adiciona a variável no escopo atual.
     * @return {@code true} se adicionada com sucesso;
     *         {@code false} se já existe no mesmo escopo (redeclaração).
     */
    public boolean addNaTabela(Variavel variavel) {
        if (pilhaEscopos.isEmpty()) return false;
        Map<String, Variavel> escopoAtual = pilhaEscopos.peek();
        if (escopoAtual.containsKey(variavel.nome())) {
            return false;
        }
        escopoAtual.put(variavel.nome(), variavel);
        return true;
    }

    /**
     * Marca uma variável como inicializada (chamado após atribuição bem-sucedida).
     * Percorre a pilha do escopo mais interno para o externo.
     */
    public void marcarInicializado(String nome) {
        for (Map<String, Variavel> escopo : pilhaEscopos) {
            Variavel v = escopo.get(nome);
            if (v != null) {
                v.setInicializado(true);
                return;
            }
        }
    }


    /**
     * Imprime todas as variáveis de todos os escopos (já fechados),
     * agrupadas por nível e ordenadas por linha de declaração.
     * Chamado apenas no final da análise.
     */
    public void printTabela() {
        historico.stream()
                .sorted((a, b) -> {
                    if (a.nivel() != b.nivel()) return Integer.compare(a.nivel(), b.nivel());
                    return Integer.compare(a.linha(), b.linha());
                })
                .forEach(v -> IO.println(v.toString()));
    }
}
