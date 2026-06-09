package org.gabriel;


public class Gramatica {
    Tokens tokens;
    private int erros = 0;
    Tabela tabela;
    private TipoVar tipoAtual;  // Rastreia o tipo sendo declarado

    public Gramatica(Tokens tokens) {
        this.tokens = tokens;
        tabela = new Tabela();
    }

    /** Retorna o número de erros encontrados durante a análise. */
    public int getErros() {
        return erros;
    }

    public void incrementarErros() {
        erros++;
    }

    private void lerProxSeguro(String contexto) throws Exception {
        if (!tokens.temProximo()) {
            Token t = tokens.getTokenAtual();
            throw new Exception("ERRO fim de entrada em <" + contexto + "> em linha " + t.linha() + ", coluna " + t.coluna() + " (token: '" + t.valor() + "')");
        }
        tokens.lerProx();
    }
    public void programa() throws Exception {
        bloco();
        tabela.printTabela();
    }


    public void bloco() throws Exception {
        if (!this.tokens.getTokenAtual().valor().equals("{")) {
            Token t = this.tokens.getTokenAtual();
            throw new Exception("ERRO <bloco> Falta { em linha " + t.linha() + ", coluna " + t.coluna() + " (token: '" + t.valor() + "')");
        }
        this.tokens.lerProx();

        tabela.abrirEscopo(); // ← abre novo escopo
        listaComandos();
        tabela.fecharEscopo(); // ← fecha e imprime variáveis do escopo

        if (!this.tokens.getTokenAtual().valor().equals("}")) {
            Token t = this.tokens.getTokenAtual();
            throw new Exception("ERRO <bloco> falta } em linha " + t.linha() + ", coluna " + t.coluna() + " (token: '" + t.valor() + "')");
        }
        if (tokens.temProximo()) {
            this.tokens.lerProx(); // consome o '}'
        }
    }
    public void listaComandos() throws Exception {

        if (tokens.getTokenAtual().valor().equals("}") || tokens.isEOF()) {
            return;
        }

        if (!ehInicioComando()) {
            Token t = tokens.getTokenAtual();
            throw new Exception("ERRO Token inesperado em <ListaComandos>: '" + t.valor() + "' em linha " + t.linha() + ", coluna " + t.coluna());
        }

        try {
            comando();
            // Cada comando já consome seu próprio terminador (';' ou '}')
        } catch (Exception e) {
            // --- Recuperação de erros (modo pânico) ---
            erros++;
            IO.println(e.getMessage());

            // Avança até o próximo ";"
            tokens.pularAteSeguro();

            // Se encontrou ";", consome-o e continua; se EOF, encerra
            if (!tokens.isEOF()) {
                tokens.lerProx(); // consome o ";"
            } else {
                return;
            }
        }

        listaComandos();
    }

    private boolean ehInicioComando() throws Exception {
        var token = tokens.getTokenAtual();
        return token.tipoToken() == TipoToken.IDENTIFICADOR
                || token.valor().equals("if")
                || token.valor().equals("while")
                || token.valor().equals("for")
                || token.valor().equals("{")
                || tipo()
                || token.valor().equals(";");
    }

    public void comando() throws Exception {
        if (tokens.getTokenAtual().valor().equals(";")) {return;}

        else if(tokens.getTokenAtual().tipoToken().equals(TipoToken.IDENTIFICADOR)) {
            atribuicao();
            return;
        }

        else if(tokens.getTokenAtual().valor().equals("if")){
            condicao();
            return;
        }

        else if(tokens.getTokenAtual().valor().equals("while")||
                tokens.getTokenAtual().valor().equals("for")){
            repeticao();
            return;
        }
        else if(tipo()){
            declaracao();
            return;
        }
        else  if(tokens.getTokenAtual().valor().equals("{")){
            bloco();
            return;
        }
        Token t = tokens.getTokenAtual();
        throw new Exception("ERRO Token inesperado em <Comando>: '" + t.valor() + "' em linha " + t.linha() + ", coluna " + t.coluna());

    }
    public void declaracao() throws Exception {
            if (!tipo()) {
                Token t = tokens.getTokenAtual();
                throw new Exception("ERRO esperava tipo em <Declaracao> em linha " +
                        t.linha() + ", coluna " + t.coluna());
            }
            // Captura o tipo atual
            String tipoValor = tokens.getTokenAtual().valor();
            tipoAtual = converterParaTipoVar(tipoValor);

            tokens.lerProx();
            listaDeclaracao();
            if (!tokens.getTokenAtual().valor().equals(";") ){
                var  t = tokens.getTokenAtual();
                throw new Exception("ERRO Falta \";\" "+ " em linha " + t.linha() + ", coluna " + t.coluna());
            }
            tokens.lerProx(); // consome o ';'
    }
    public void listaDeclaracao() throws Exception{
        declarador();
        listaDeclaracao1();
    }
    public void listaDeclaracao1() throws Exception{

        if (tokens.getTokenAtual().valor().equals(",") ){
            tokens.lerProx();
            declarador();
            listaDeclaracao1();
        }
        //vazio

    }
    public void declarador() throws Exception {
        if (!tokens.getTokenAtual().tipoToken().equals(TipoToken.IDENTIFICADOR)) {
            var t = tokens.getTokenAtual();
            throw new Exception("ERRO esperava IDENTIFICADOR em <Declarador> em linha " +
                    t.linha() + ", coluna " + t.coluna() + " (token: '" + t.valor() + "') ");
        }
        // Captura dados do IDENTIFICADOR
        Token tokenId = tokens.getTokenAtual();
        String nomeVar = tokenId.valor();
        Integer linha = tokenId.linha();
        Integer coluna = tokenId.coluna();

        tokens.lerProx();
        declarador1();

        // Adiciona no escopo atual com o nível correto
        Variavel var = new Variavel(nomeVar, tipoAtual, linha, coluna, tabela.nivelAtual());
        boolean adicionado = tabela.addNaTabela(var);
        if (!adicionado) {
            IO.println("AVISO variável '" + nomeVar + "' já foi declarada neste escopo em linha " + linha + ", coluna " + coluna);
        }
    }
    public void declarador1() throws Exception {
        if (tokens.getTokenAtual().valor().equals("=")) {
            tokens.lerProx();
            TipoVar tipoExpr = expressao();
            if (TipoVar.compativel(tipoAtual, tipoExpr) == null) {
                throw new Exception("ERRO tipo incompativel na inicializacao: tipo declarado e "
                        + tipoAtual + " mas expressao retorna " + tipoExpr);
            }
        }
    }

    public boolean tipo() throws Exception {
        var valor = tokens.getTokenAtual().valor();
        return valor.equals("int") || valor.equals("float") || valor.equals("char") || valor.equals("bool");
    }

    private TipoVar converterParaTipoVar(String tipo) {
        return switch(tipo) {
            case "int" -> TipoVar.INT;
            case "float" -> TipoVar.FLOAT;
            case "char" -> TipoVar.CHAR;
            case "bool" -> TipoVar.BOOL;
            default -> null;
        };
    }
    public void atribuicao() throws Exception {
        if (!tokens.getTokenAtual().tipoToken().equals(TipoToken.IDENTIFICADOR)) {
            Token t = tokens.getTokenAtual();
            throw new Exception("ERRO esperava IDENTIFICADOR em <atribuicao> em linha " + t.linha() + ", coluna " + t.coluna() + " (token: '" + t.valor() + "') ");
        }
        String nomeVar = tokens.getTokenAtual().valor();
        Token tokenVar = tokens.getTokenAtual();
        Variavel varDecl = tabela.buscar(nomeVar);
        if (varDecl == null) {
            throw new Exception("ERRO variavel '" + nomeVar + "' nao foi declarada em linha " + tokenVar.linha() + ", coluna " + tokenVar.coluna());
        }
        TipoVar tipoVar = varDecl.tipo();

        tokens.lerProx();
        if (!tokens.getTokenAtual().valor().equals("=")) {
            Token t = tokens.getTokenAtual();
            throw new Exception("ERRO esperava \"=\" em <atribuicao> em linha " + t.linha() + ", coluna " + t.coluna() + " (token: '" + t.valor() + "') ");
        }
        tokens.lerProx();
        TipoVar tipoExpr = expressao();
        if (TipoVar.compativel(tipoVar, tipoExpr) == null) {
            throw new Exception("ERRO tipo incompativel na atribuicao: variavel '"
                    + nomeVar + "' e do tipo " + tipoVar
                    + " mas expressao retorna " + tipoExpr
                    + " em linha " + tokenVar.linha() + ", coluna " + tokenVar.coluna());
        }
        if (!tokens.getTokenAtual().valor().equals(";")) {
            Token t = tokens.getTokenAtual();
            throw new Exception("ERRO Falta \";\" em <atribuicao> em linha " + t.linha() + ", coluna " + t.coluna() + " (token: '" + t.valor() + "') ");
        }
        tokens.lerProx(); // consome o ';'
    }

     public void condicao() throws Exception {
         if(!tokens.getTokenAtual().valor().equals("if")){
             Token t = tokens.getTokenAtual();
             throw  new Exception("ERRO esperava \"if\" em <condicao> em linha " + t.linha() + ", coluna " + t.coluna() + " (token: '" + t.valor() + "') ");
         }
         tokens.lerProx();
         if(!tokens.getTokenAtual().valor().equals("(")){
             Token t = tokens.getTokenAtual();
             throw  new Exception("ERRO esperava \"(\" em <condicao> em linha " + t.linha() + ", coluna " + t.coluna() + " (token: '" + t.valor() + "') ");
         }
         tokens.lerProx();
         expressao();

         if(!tokens.getTokenAtual().valor().equals(")")){
             Token t = tokens.getTokenAtual();
             throw  new Exception("ERRO esperava \")\" em <condicao> em linha " + t.linha() + ", coluna " + t.coluna() + " (token: '" + t.valor() + "') ");
         }
         tokens.lerProx();
         bloco(); // bloco() ja consome o '}'
         senao();

     }

    public void senao() throws Exception {
        if(!tokens.getTokenAtual().valor().equals("else")){
            return; // Sem else
        }
        tokens.lerProx();
        bloco();
    }

    public void repeticao() throws Exception {
         if(tokens.getTokenAtual().valor().equals("while")){
             lerProxSeguro("Repeticao");  // consume "while"
             if(!tokens.getTokenAtual().valor().equals("(")){
                 Token t = tokens.getTokenAtual();
                 throw  new Exception("ERRO esperava \"(\" em <repeticao> em linha "
                         + t.linha() + ", coluna " + t.coluna() + " (token: '" + t.valor() + "') ");
             }
             lerProxSeguro("Repeticao");  // consume "("
             expressao();
             if(!tokens.getTokenAtual().valor().equals(")")){
                 Token t = tokens.getTokenAtual();
                 throw  new Exception("ERRO esperava \")\" em <repeticao> em linha "
                         + t.linha() + ", coluna " + t.coluna() + " (token: '" + t.valor() + "') ");

             }
             lerProxSeguro("Repeticao");  // consume ")"
             bloco();
             return;
         }
         if(tokens.getTokenAtual().valor().equals("for")){
             lerProxSeguro("Repeticao");  // consume "for"
             if(!tokens.getTokenAtual().valor().equals("(")){
                 Token t = tokens.getTokenAtual();
                 throw  new Exception("ERRO esperava \"(\" em <repeticao> em linha "
                         + t.linha() + ", coluna " + t.coluna() + " (token: '" + t.valor() + "') ");
             }
             lerProxSeguro("Repeticao");  // consume "("
             for1();
             if(!tokens.getTokenAtual().valor().equals(";")){
                 Token t = tokens.getTokenAtual();
                 throw  new Exception("ERRO esperava \";\" em <repeticao> em linha "
                         + t.linha() + ", coluna " + t.coluna() + " (token: '" + t.valor() + "') ");
             }
             lerProxSeguro("Repeticao");  // consume ";"
             for2();
             if(!tokens.getTokenAtual().valor().equals(";")){
                 Token t = tokens.getTokenAtual();
                 throw  new Exception("ERRO esperava \";\" em <repeticao> em linha "
                         + t.linha() + ", coluna " + t.coluna() + " (token: '" + t.valor() + "') ");
             }
             lerProxSeguro("Repeticao");  // consume ";"
             for3();
             if(!tokens.getTokenAtual().valor().equals(")")){
                 Token t = tokens.getTokenAtual();
                 throw  new Exception("ERRO esperava \")\" em <repeticao> em linha "
                         + t.linha() + ", coluna " + t.coluna() + " (token: '" + t.valor() + "') ");
             }
             lerProxSeguro("Repeticao");  // consume ")"
             bloco();
             return;
         }
     }
      public void for1() throws Exception {
         if (tokens.getTokenAtual().valor().equals(";")) {
             return;
         }
         if (tipo()) {
             String tipoValor = tokens.getTokenAtual().valor();
             tipoAtual = converterParaTipoVar(tipoValor);
             tokens.lerProx();
             listaDeclaracao();
             return;
         }
         if (tokens.getTokenAtual().tipoToken().equals(TipoToken.IDENTIFICADOR)) {
             String nomeVar = tokens.getTokenAtual().valor();
             Token tokenVar = tokens.getTokenAtual();
             Variavel varDecl = tabela.buscar(nomeVar);
             if (varDecl == null) {
                 throw new Exception("ERRO variavel '" + nomeVar + "' nao foi declarada em linha " + tokenVar.linha() + ", coluna " + tokenVar.coluna());
             }
             TipoVar tipoId = varDecl.tipo();
             tokens.lerProx();
             if (tokens.getTokenAtual().valor().equals("=")) {
                 tokens.lerProx();
                 TipoVar tipoExpr = expressao();
                 if (TipoVar.compativel(tipoId, tipoExpr) == null) {
                     throw new Exception("ERRO tipo incompativel no for: variavel '"
                             + nomeVar + "' e do tipo " + tipoId
                             + " mas expressao retorna " + tipoExpr
                             + " em linha " + tokenVar.linha() + ", coluna " + tokenVar.coluna());
                 }
                 return;
             }
             TipoVar t = mult(tipoId);
             t = soma(t);
             logico(t);
             return;
         }
         expressao();
         return;
      }
    public void for2() throws Exception {
        if (tokens.getTokenAtual().valor().equals(";")){
            return;
        }
        expressao();
    }
     public void for3() throws Exception {
         if (tokens.getTokenAtual().valor().equals(")")) {
             return;
         }
         if (tokens.getTokenAtual().tipoToken().equals(TipoToken.IDENTIFICADOR)) {
             String nomeVar = tokens.getTokenAtual().valor();
             Token tokenVar = tokens.getTokenAtual();
             Variavel varDecl = tabela.buscar(nomeVar);
             if (varDecl == null) {
                 throw new Exception("ERRO variavel '" + nomeVar + "' nao foi declarada em linha " + tokenVar.linha() + ", coluna " + tokenVar.coluna());
             }
             TipoVar tipoId = varDecl.tipo();
             tokens.lerProx();
             if (tokens.getTokenAtual().valor().equals("=")) {
                 tokens.lerProx();
                 TipoVar tipoExpr = expressao();
                 if (TipoVar.compativel(tipoId, tipoExpr) == null) {
                     throw new Exception("ERRO tipo incompativel no for: variavel '"
                             + nomeVar + "' e do tipo " + tipoId
                             + " mas expressao retorna " + tipoExpr
                             + " em linha " + tokenVar.linha() + ", coluna " + tokenVar.coluna());
                 }
                 return;
             }
             TipoVar t = mult(tipoId);
             t = soma(t);
             logico(t);
             return;
         }
         expressao();
     }
    public TipoVar expressao() throws Exception {
        TipoVar tipo = arit();
        return logico(tipo);
    }

    public TipoVar logico(TipoVar tipoEsq) throws Exception {
        if (tokens.getTokenAtual().tipoToken().equals(TipoToken.OPERADOR_LOGICO)) {
            Token op = tokens.getTokenAtual();
            lerProxSeguro("Logico");
            TipoVar tipoDir = arit();
            TipoVar compatibilidade = TipoVar.compativel(tipoEsq, tipoDir);
            if (compatibilidade == null) {
                throw new Exception("ERRO tipos incompativeis em operacao logica: "
                        + tipoEsq + " e " + tipoDir
                        + " em linha " + op.linha() + ", coluna " + op.coluna());
            }
            return logico(TipoVar.BOOL); // operacao logica sempre retorna bool
        }
        return tipoEsq;
    }

    public TipoVar arit() throws Exception {
        TipoVar tipo = termo();
        return soma(tipo);
    }

    public TipoVar soma(TipoVar tipoEsq) throws Exception {
        if (soma1()) {
            Token op = tokens.getTokenAtual();
            lerProxSeguro("Soma");
            TipoVar tipoDir = termo();
            TipoVar resultado = TipoVar.compativel(tipoEsq, tipoDir);
            if (resultado == null) {
                throw new Exception("ERRO tipos incompativeis em '+'/'-': "
                        + tipoEsq + " e " + tipoDir
                        + " em linha " + op.linha() + ", coluna " + op.coluna());
            }
            return soma(resultado);
        }
        return tipoEsq;
    }

    public boolean soma1() throws Exception {
        return tokens.getTokenAtual().valor().equals("+") ||
                tokens.getTokenAtual().valor().equals("-");
    }

    public TipoVar termo() throws Exception {
        TipoVar tipo = fator();
        return mult(tipo);
    }

    public TipoVar mult(TipoVar tipoEsq) throws Exception {
        if (mult1()) {
            Token op = tokens.getTokenAtual();
            lerProxSeguro("Mult");
            TipoVar tipoDir = fator();
            TipoVar resultado = TipoVar.compativel(tipoEsq, tipoDir);
            if (resultado == null) {
                throw new Exception("ERRO tipos incompativeis em '*'/'/': "
                        + tipoEsq + " e " + tipoDir
                        + " em linha " + op.linha() + ", coluna " + op.coluna());
            }
            return mult(resultado);
        }
        return tipoEsq;
    }

    public boolean mult1() throws Exception {
        return tokens.getTokenAtual().valor().equals("*") ||
                tokens.getTokenAtual().valor().equals("/");
    }

    public TipoVar fator() throws Exception {
          var atual = tokens.getTokenAtual();

          if (atual.tipoToken().equals(TipoToken.IDENTIFICADOR)) {
              Variavel v = tabela.buscar(atual.valor());
              if (v == null) {
                  throw new Exception("ERRO variavel '" + atual.valor() + "' nao foi declarada em linha " + atual.linha() + ", coluna " + atual.coluna());
              }
              lerProxSeguro("Fator");
              return v.tipo();
          }
          else if (atual.tipoToken().equals(TipoToken.NUMERO)) {
              // Distingue int de float pelo ponto decimal
              TipoVar tipo = atual.valor().contains(".") ? TipoVar.FLOAT : TipoVar.INT;
              lerProxSeguro("Fator");
              return tipo;
          }
          else if (atual.tipoToken().equals(TipoToken.LITERAL)) {
              lerProxSeguro("Fator");
              return TipoVar.CHAR;
          }
          else if (atual.valor().equals("True") || atual.valor().equals("False")) {
              lerProxSeguro("Fator");
              return TipoVar.BOOL;
          }
          else if (atual.valor().equals("(")) {
              lerProxSeguro("Fator");  // consume "("
              TipoVar tipo = expressao();
              if (!tokens.getTokenAtual().valor().equals(")")) {
                  Token t = tokens.getTokenAtual();
                  throw new Exception("ERRO esperava \")\" em <fator> em linha "
                          + t.linha() + ", coluna " + t.coluna() + " (token: '" + t.valor() + "') ");
              }
              lerProxSeguro("Fator");  // consume ")"
              return tipo;
          }
          Token t = tokens.getTokenAtual();
          throw new Exception("ERRO expressao invalida em linha "
                  + t.linha() + ", coluna " + t.coluna() + " (token: '" + t.valor() + "') ");
     }

}
/*
<Programa> ::= <Bloco>

<Bloco> ::= "{" <ListaComandos> "}"

<ListaComandos> ::= <Comando> <ListaComandos> | ε

<Comando> ::= <Atribuicao> | <Condicao> | <Repeticao> | <Bloco> | <Declaracao> | ";"

<Declaracao> ::= <Tipo> <ListaDeclaracao> ";"
<ListaDeclaracao> ::= <Declarador> <ListaDeclaracao'>
<ListaDeclaracao'> ::= "," <Declarador> <ListaDeclaracao'> | ε
<Declarador> ::= IDENTIFICADOR <Declarador'>
<Declarador'> ::= "=" <Expressao> | ε

<Tipo> ::= "int" | "float" | "char" | "bool"

<Atribuicao> ::= IDENTIFICADOR "=" <Expressao>

<Condicao> ::= "if" "(" <Expressao> ")" <Bloco> <Senao>

<Senao> ::= "else" <Bloco> | ε

<Repeticao> ::= "while" "(" <Expressao> ")" <Bloco> |
                 "for" "(" <For1> ";" <For2> ";" <For3> ")" <Bloco>

<For1> ::= <Tipo> <ListaDeclaracao> | IDENTIFICADOR "=" <Expressao> | <Expressao> | ε
<For2> ::= <Expressao> | ε
<For3> ::= <Expressao> | ε

<Expressao> ::= <Arit> <Logico>
<Logico> ::= OPERADOR_LOGICO <Arit> <Logico> | ε

<Arit> ::= <Termo> <Soma>

<Soma> ::= "+" <Termo> <Soma> | "-" <Termo> <Soma> | ε

<Termo> ::= <Fator> <Mult>
<Mult> ::= "*" <Fator> <Mult> | "/" <Fator> <Mult> | ε

<Fator> ::= IDENTIFICADOR | NUMERO | LITERAL | "True" | "False" | "(" <Expressao> ")"
 */
