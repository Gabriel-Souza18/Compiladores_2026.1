set -euo pipefail

# ---------------------------------------------------------------------------
# Cores para saída bonita
# ---------------------------------------------------------------------------
BOLD='\033[1m'
CYAN='\033[0;36m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
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
# Caminhos dos módulos (relativos ao diretório deste script)
# ---------------------------------------------------------------------------
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

LEXICO_DIR="$SCRIPT_DIR/AnalisadorLexico"
SINTATICO_DIR="$SCRIPT_DIR/AnalisadorSintatico"

LEXICO_ENTRADA="$LEXICO_DIR/src/main/java/org/gabriel/codigoFonte.txt"
LEXICO_SAIDA="$LEXICO_DIR/src/main/java/org/gabriel/tokens.txt"
SINTATICO_ENTRADA="$SINTATICO_DIR/src/main/java/org/gabriel/Entrada.txt"

# ---------------------------------------------------------------------------
# Cabeçalho
# ---------------------------------------------------------------------------

echo -e "  Código-fonte : ${YELLOW}${CODIGO_FONTE}${RESET}"
echo ""

# ---------------------------------------------------------------------------
# Passo 1 – Copia o código-fonte para onde o Léxico espera
# ---------------------------------------------------------------------------
echo -e "${BOLD} Preparando entrada do Léxico...${RESET}"
cp "$CODIGO_FONTE" "$LEXICO_ENTRADA"
echo -e "  Arquivo copiado para: $LEXICO_ENTRADA"
echo ""

# ---------------------------------------------------------------------------
# Passo 2 – Compila o Léxico
# ---------------------------------------------------------------------------
echo -e "${BOLD} Compilando AnalisadorLexico...${RESET}"
mvn -q -f "$LEXICO_DIR/pom.xml" compile 2>/dev/null
echo -e "  Compilado com sucesso."
echo ""

# ---------------------------------------------------------------------------
# Passo 3 – Roda o Léxico  (cd para o diretório do projeto para que os
#            caminhos relativos no código Java sejam resolvidos corretamente)
# ---------------------------------------------------------------------------
echo -e "${BOLD} Executando Analisador Léxico...${RESET}"

set +e
(cd "$LEXICO_DIR" && java -cp target/classes org.gabriel.Main)
LEXICO_EXIT=$?
set -e



if [ $LEXICO_EXIT -ne 0 ]; then
    echo -e "  ${RED}✘ Analisador Léxico terminou com erro (código $LEXICO_EXIT).${RESET}"
    echo -e "    Verifique o código-fonte e tente novamente."
    exit $LEXICO_EXIT
fi

if [ ! -f "$LEXICO_SAIDA" ]; then
    echo -e "${RED}Erro: o Léxico não gerou o arquivo de tokens: $LEXICO_SAIDA${RESET}"
    exit 1
fi

echo ""
echo -e "  Tokens gerados em: $LEXICO_SAIDA"
echo ""

# Exibe os tokens gerados
echo -e "${BOLD}  Tokens produzidos:${RESET}"
echo ""
cat "$LEXICO_SAIDA"
echo ""

# ---------------------------------------------------------------------------
# Passo 4 – Copia tokens para onde o Sintático espera
# ---------------------------------------------------------------------------
echo -e "${BOLD} Passando tokens para o Analisador Sintático...${RESET}"
cp "$LEXICO_SAIDA" "$SINTATICO_ENTRADA"
echo -e "  Tokens copiados para: $SINTATICO_ENTRADA"
echo ""

# ---------------------------------------------------------------------------
# Passo 5 – Compila e roda o Sintático
# ---------------------------------------------------------------------------
echo -e "${BOLD} Compilando e executando AnalisadorSintatico...${RESET}"
mvn -q -f "$SINTATICO_DIR/pom.xml" compile 2>/dev/null


set +e
(cd "$SINTATICO_DIR" && java -cp target/classes org.gabriel.Main)
SINTATICO_EXIT=$?
set -e


echo ""
