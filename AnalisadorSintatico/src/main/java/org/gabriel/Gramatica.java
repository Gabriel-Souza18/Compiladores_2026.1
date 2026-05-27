package org.gabriel;


public class Gramatica {
    Tokens tokens;
    private int erros = 0;

    public Gramatica(Tokens tokens) {
        this.tokens = tokens;
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
    }


    public void bloco() throws Exception{
        if (!this.tokens.getTokenAtual().valor().equals("{")){
            Token t = this.tokens.getTokenAtual();
            throw new Exception("ERRO <bloco> Falta { em linha " + t.linha() + ", coluna " + t.coluna() + " (token: '" + t.valor() + "')");
        }
        this.tokens.lerProx();
        listaComandos();

        if (!this.tokens.getTokenAtual().valor().equals("}")){
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
        return token.tipo() == Tipo.IDENTIFICADOR
                || token.valor().equals("if")
                || token.valor().equals("while")
                || token.valor().equals("for")
                || token.valor().equals("{")
                || tipo()
                || token.valor().equals(";");
    }

    public void comando() throws Exception {
        if (tokens.getTokenAtual().valor().equals(";")) {return;}

        else if(tokens.getTokenAtual().tipo().equals(Tipo.IDENTIFICADOR)) {
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
    public void declarador() throws Exception{
        if(!tokens.getTokenAtual().tipo().equals(Tipo.IDENTIFICADOR)){
            var t = tokens.getTokenAtual();
            throw new Exception("ERRO esperava IDENTIFICADOR em <Declarador> em linha " +
                    t.linha() + ", coluna " + t.coluna() + " (token: '" + t.valor() + "') ");
        }
        tokens.lerProx();
        declarador1();
    }
    public void declarador1() throws Exception{

        if (tokens.getTokenAtual().valor().equals("=") ){
            tokens.lerProx();
            expressao();

        }

    }

    public boolean tipo() throws Exception {
        var valor = tokens.getTokenAtual().valor();
        return valor.equals("int") || valor.equals("float") || valor.equals("char") || valor.equals("bool");
    }
    public void atribuicao() throws Exception {
        if(!tokens.getTokenAtual().tipo().equals(Tipo.IDENTIFICADOR)) {
            Token t = tokens.getTokenAtual();
            throw new Exception("ERRO esperava IDENTIFICADOR em <atribuicao> em linha " + t.linha() + ", coluna " + t.coluna() + " (token: '" + t.valor() + "') ");
        }
        tokens.lerProx();
        if(!tokens.getTokenAtual().valor().equals("=")){
            Token t = tokens.getTokenAtual();
            throw new Exception("ERRO esperava \"=\" em <atribuicao> em linha " + t.linha() + ", coluna " + t.coluna() + " (token: '" + t.valor() + "') ");
        }
        tokens.lerProx();
        expressao();
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
        if (tokens.getTokenAtual().valor().equals(";")){
            return;
        }
        if(tipo()){
            tokens.lerProx();
            listaDeclaracao();
            return;
        }
        if(tokens.getTokenAtual().tipo().equals(Tipo.IDENTIFICADOR)){
            tokens.lerProx();
            if(tokens.getTokenAtual().valor().equals("=")){
                tokens.lerProx();
                expressao();
                return;
            }
            mult();
            soma();
            logico();
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
         if (tokens.getTokenAtual().valor().equals(")")){
             return;
         }
         if(tokens.getTokenAtual().tipo().equals(Tipo.IDENTIFICADOR)){
             tokens.lerProx();
             if(tokens.getTokenAtual().valor().equals("=")){
                 tokens.lerProx();
                 expressao();
                 return;
             }
             mult();
             soma();
             logico();
             return;
         }
         expressao();
     }
    public void expressao() throws Exception {
         arit();
         logico();
         return;
     }

     public void logico()throws Exception {
        if(tokens.getTokenAtual().tipo().equals(Tipo.OPERADOR_LOGICO)){
           lerProxSeguro("Logico");
           arit();
           logico();
           return;
        }
        return;
     }
    public void arit() throws Exception {
         termo();
         soma();
     }

    public void soma() throws Exception {
         if (soma1()){
             lerProxSeguro("Soma");
             termo();
             soma();
         }
     }
    public boolean soma1() throws Exception {
        return tokens.getTokenAtual().valor().equals("+") ||
                tokens.getTokenAtual().valor().equals("-");
    }
    public void termo() throws Exception {
         fator();
         mult();
     }
     public void mult() throws Exception {
        if( mult1()) {
            lerProxSeguro("Mult");
            fator();
            mult();
        }
     }
    public boolean mult1() throws Exception {
        return tokens.getTokenAtual().valor().equals("*") ||
                tokens.getTokenAtual().valor().equals("/");
    }

    public void fator() throws Exception {
         var atual = tokens.getTokenAtual();

         if(atual.tipo().equals(Tipo.IDENTIFICADOR)||
                 atual.tipo().equals(Tipo.NUMERO)||
                 atual.tipo().equals(Tipo.LITERAL)||
                 atual.valor().equals("False")||
                 atual.valor().equals("True")){
             lerProxSeguro("Fator");  // consume terminal
             return;
         } else if (atual.valor().equals("(")) {
             lerProxSeguro("Fator");  // consume "("
             expressao();
             if (!tokens.getTokenAtual().valor().equals(")")){
                 Token t = tokens.getTokenAtual();
                 throw  new Exception("ERRO esperava \")\" em <fator> em linha "
                         + t.linha() + ", coluna " + t.coluna() + " (token: '" + t.valor() + "') ");

             }
             lerProxSeguro("Fator");  // consume ")"
             return;
         }
         Token t = tokens.getTokenAtual();
         throw  new Exception("ERRO expressao invalida em linha "
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
