#!/usr/bin/env bash
# =============================================================================
# run.sh  –  Orquestrador Léxico → Sintático
#
# Uso:
#   ./run.sh <arquivo_de_codigo_fonte>
#
# O script:
#   1. Compila e roda o Léxico passando o arquivo de entrada via args
#      (gera um arquivo de tokens temporário)
#   2. Compila e roda o Sintático passando o arquivo de tokens via args
# =============================================================================

set -euo pipefail

BOLD='\033[1m'
CYAN='\033[0;36m'
RED='\033[0;31m'
RESET='\033[0m'

sep() { echo -e "${CYAN}──────────────────────────────────────────────────${RESET}"; }

# ---------------------------------------------------------------------------
# Valida argumento
# ---------------------------------------------------------------------------
if [ $# -lt 1 ]; then
    echo -e "${RED}Uso: $0 <arquivo_de_codigo_fonte>${RESET}"
    echo -e "  Exemplo: ${BOLD}$0 exemplo.txt${RESET}"
    exit 1
fi

CODIGO_FONTE="$(realpath "$1")"

if [ ! -f "$CODIGO_FONTE" ]; then
    echo -e "${RED}Erro: arquivo não encontrado: '$CODIGO_FONTE'${RESET}"
    exit 1
fi

# ---------------------------------------------------------------------------
# Caminhos dos módulos
# ---------------------------------------------------------------------------
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

LEXICO_DIR="$SCRIPT_DIR/AnalisadorLexico"
SINTATICO_DIR="$SCRIPT_DIR/AnalisadorSintatico"

# Arquivo de tokens intermediário (absoluto, sem ambiguidade de diretório)
TOKENS_FILE="$SCRIPT_DIR/tokens_intermediario.txt"

# ---------------------------------------------------------------------------
# Cabeçalho
# ---------------------------------------------------------------------------
echo ""
sep
echo -e "  ${BOLD}Compiladores 2026.1 – Pipeline Léxico → Sintático${RESET}"
sep
echo -e "  Código-fonte : $CODIGO_FONTE"
echo ""

# ---------------------------------------------------------------------------
# Passo 1 – Compila o Léxico
# ---------------------------------------------------------------------------
echo -e "${BOLD}[1/3] Compilando AnalisadorLexico...${RESET}"
mvn -q -f "$LEXICO_DIR/pom.xml" compile 2>/dev/null
echo -e "  Compilado."
echo ""

# ---------------------------------------------------------------------------
# Passo 2 – Roda o Léxico via mvn exec:java -Dexec.args
#            args[0] = arquivo de entrada
#            args[1] = arquivo de saída dos tokens
# ---------------------------------------------------------------------------
echo -e "${BOLD}[2/3] Executando Analisador Léxico...${RESET}"
sep

set +e
mvn -q -f "$LEXICO_DIR/pom.xml" exec:java \
    -Dexec.mainClass="org.gabriel.Main" \
    -Dexec.args="$CODIGO_FONTE $TOKENS_FILE" \
    2>&1
LEXICO_EXIT=$?
set -e

sep

if [ $LEXICO_EXIT -ne 0 ]; then
    echo -e "  ${RED}Analisador Léxico terminou com erro (código $LEXICO_EXIT).${RESET}"
    exit $LEXICO_EXIT
fi

if [ ! -f "$TOKENS_FILE" ]; then
    echo -e "${RED}Erro: arquivo de tokens não foi gerado: $TOKENS_FILE${RESET}"
    exit 1
fi

echo ""
echo -e "  Tokens gerados em: $TOKENS_FILE"
echo ""
echo -e "${BOLD}  Tokens produzidos:${RESET}"
echo ""
cat "$TOKENS_FILE"
echo ""

# ---------------------------------------------------------------------------
# Passo 3 – Compila e roda o Sintático via mvn exec:java -Dexec.args
#            args[0] = arquivo de tokens
# ---------------------------------------------------------------------------
echo -e "${BOLD}[3/3] Compilando e executando AnalisadorSintatico...${RESET}"
mvn -q -f "$SINTATICO_DIR/pom.xml" compile 2>/dev/null
sep

set +e
mvn -q -f "$SINTATICO_DIR/pom.xml" exec:java \
    -Dexec.mainClass="org.gabriel.Main" \
    -Dexec.args="$TOKENS_FILE" \
    2>&1
SINTATICO_EXIT=$?
set -e

sep
echo ""

if [ $SINTATICO_EXIT -ne 0 ]; then
    echo -e "  ${RED}Analisador Sintático terminou com erro (código $SINTATICO_EXIT).${RESET}"
fi

echo ""
