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
            throw new Exception("ERRO fim de entrada em <" + contexto + "> em linha " + t.getLinha() + ", coluna " + t.getColuna() + " (token: '" + t.getValor() + "')");
        }
        tokens.lerProx();
    }
    public void programa() throws Exception {
        bloco();
    }


    public void bloco() throws Exception{
        if (!this.tokens.getTokenAtual().getValor().equals("{")){
            Token t = this.tokens.getTokenAtual();
            throw new Exception("ERRO <bloco> Falta { em linha " + t.getLinha() + ", coluna " + t.getColuna() + " (token: '" + t.getValor() + "')");
        }
        this.tokens.lerProx();
        listaComandos();

        if (!this.tokens.getTokenAtual().getValor().equals("}")){
            Token t = this.tokens.getTokenAtual();
            throw new Exception("ERRO <bloco> falta } em linha " + t.getLinha() + ", coluna " + t.getColuna() + " (token: '" + t.getValor() + "')");
        }
        if (tokens.temProximo()) {
            this.tokens.lerProx(); // consome o '}'
        }

    }
    public void listaComandos() throws Exception {

        if (tokens.getTokenAtual().getValor().equals("}") || tokens.isEOF()) {
            return;
        }

        if (!ehInicioComando()) {
            Token t = tokens.getTokenAtual();
            throw new Exception("ERRO Token inesperado em <ListaComandos>: '" + t.getValor() + "' em linha " + t.getLinha() + ", coluna " + t.getColuna());
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
        return token.getTipo() == Tipo.IDENTIFICADOR
                || token.getValor().equals("if")
                || token.getValor().equals("while")
                || token.getValor().equals("for")
                || token.getValor().equals("{")
                || tipo()
                || token.getValor().equals(";");
    }

    public void comando() throws Exception {
        if (tokens.getTokenAtual().getValor().equals(";")) {return;}

        else if(tokens.getTokenAtual().getTipo().equals(Tipo.IDENTIFICADOR)) {
            atribuicao();
            return;
        }

        else if(tokens.getTokenAtual().getValor().equals("if")){
            condicao();
            return;
        }

        else if(tokens.getTokenAtual().getValor().equals("while")||
                tokens.getTokenAtual().getValor().equals("for")){
            repeticao();
            return;
        }
        else if(tipo()){
            declaracao();
            return;
        }
        else  if(tokens.getTokenAtual().getValor().equals("{")){
            bloco();
            return;
        }
        Token t = tokens.getTokenAtual();
        throw new Exception("ERRO Token inesperado em <Comando>: '" + t.getValor() + "' em linha " + t.getLinha() + ", coluna " + t.getColuna());

    }
    public void declaracao() throws Exception {
            if (!tipo()) {
                Token t = tokens.getTokenAtual();
                throw new Exception("ERRO esperava tipo em <Declaracao> em linha " +
                        t.getLinha() + ", coluna " + t.getColuna());
            }
            tokens.lerProx();
            listaDeclaracao();
            if (!tokens.getTokenAtual().getValor().equals(";") ){
                var  t = tokens.getTokenAtual();
                throw new Exception("ERRO Falta \";\" "+ " em linha " + t.getLinha() + ", coluna " + t.getColuna());
            }
            tokens.lerProx(); // consome o ';'
    }
    public void listaDeclaracao() throws Exception{
        declarador();
        listaDeclaracao1();
    }
    public void listaDeclaracao1() throws Exception{

        if (tokens.getTokenAtual().getValor().equals(",") ){
            tokens.lerProx();
            declarador();
            listaDeclaracao1();
        }
        //vazio

    }
    public void declarador() throws Exception{
        if(!tokens.getTokenAtual().getTipo().equals(Tipo.IDENTIFICADOR)){
            var t = tokens.getTokenAtual();
            throw new Exception("ERRO esperava IDENTIFICADOR em <Declarador> em linha " +
                    t.getLinha() + ", coluna " + t.getColuna() + " (token: '" + t.getValor() + "') ");
        }
        tokens.lerProx();
        declarador1();
    }
    public void declarador1() throws Exception{

        if (tokens.getTokenAtual().getValor().equals("=") ){
            tokens.lerProx();
            expressao();

        }

    }

    public boolean tipo() throws Exception {
        var valor = tokens.getTokenAtual().getValor();
        return valor.equals("int") || valor.equals("float") || valor.equals("char") || valor.equals("bool");
    }
    public void atribuicao() throws Exception {
        if(!tokens.getTokenAtual().getTipo().equals(Tipo.IDENTIFICADOR)) {
            Token t = tokens.getTokenAtual();
            throw new Exception("ERRO esperava IDENTIFICADOR em <atribuicao> em linha " + t.getLinha() + ", coluna " + t.getColuna() + " (token: '" + t.getValor() + "') ");
        }
        tokens.lerProx();
        if(!tokens.getTokenAtual().getValor().equals("=")){
            Token t = tokens.getTokenAtual();
            throw new Exception("ERRO esperava \"=\" em <atribuicao> em linha " + t.getLinha() + ", coluna " + t.getColuna() + " (token: '" + t.getValor() + "') ");
        }
        tokens.lerProx();
        expressao();
        if (!tokens.getTokenAtual().getValor().equals(";")) {
            Token t = tokens.getTokenAtual();
            throw new Exception("ERRO Falta \";\" em <atribuicao> em linha " + t.getLinha() + ", coluna " + t.getColuna() + " (token: '" + t.getValor() + "') ");
        }
        tokens.lerProx(); // consome o ';'

    }

     public void condicao() throws Exception {
         if(!tokens.getTokenAtual().getValor().equals("if")){
             Token t = tokens.getTokenAtual();
             throw  new Exception("ERRO esperava \"if\" em <condicao> em linha " + t.getLinha() + ", coluna " + t.getColuna() + " (token: '" + t.getValor() + "') ");
         }
         tokens.lerProx();
         if(!tokens.getTokenAtual().getValor().equals("(")){
             Token t = tokens.getTokenAtual();
             throw  new Exception("ERRO esperava \"(\" em <condicao> em linha " + t.getLinha() + ", coluna " + t.getColuna() + " (token: '" + t.getValor() + "') ");
         }
         tokens.lerProx();
         expressao();

         if(!tokens.getTokenAtual().getValor().equals(")")){
             Token t = tokens.getTokenAtual();
             throw  new Exception("ERRO esperava \")\" em <condicao> em linha " + t.getLinha() + ", coluna " + t.getColuna() + " (token: '" + t.getValor() + "') ");
         }
         tokens.lerProx();
         bloco(); // bloco() ja consome o '}'
         senao();

     }

    public void senao() throws Exception {
        if(!tokens.getTokenAtual().getValor().equals("else")){
            return; // Sem else
        }
        tokens.lerProx();
        bloco();
    }

    public void repeticao() throws Exception {
         if(tokens.getTokenAtual().getValor().equals("while")){
             lerProxSeguro("Repeticao");  // consume "while"
             if(!tokens.getTokenAtual().getValor().equals("(")){
                 Token t = tokens.getTokenAtual();
                 throw  new Exception("ERRO esperava \"(\" em <repeticao> em linha "
                         + t.getLinha() + ", coluna " + t.getColuna() + " (token: '" + t.getValor() + "') ");
             }
             lerProxSeguro("Repeticao");  // consume "("
             expressao();
             if(!tokens.getTokenAtual().getValor().equals(")")){
                 Token t = tokens.getTokenAtual();
                 throw  new Exception("ERRO esperava \")\" em <repeticao> em linha "
                         + t.getLinha() + ", coluna " + t.getColuna() + " (token: '" + t.getValor() + "') ");

             }
             lerProxSeguro("Repeticao");  // consume ")"
             bloco();
             return;
         }
         if(tokens.getTokenAtual().getValor().equals("for")){
             lerProxSeguro("Repeticao");  // consume "for"
             if(!tokens.getTokenAtual().getValor().equals("(")){
                 Token t = tokens.getTokenAtual();
                 throw  new Exception("ERRO esperava \"(\" em <repeticao> em linha "
                         + t.getLinha() + ", coluna " + t.getColuna() + " (token: '" + t.getValor() + "') ");
             }
             lerProxSeguro("Repeticao");  // consume "("
             for1();
             if(!tokens.getTokenAtual().getValor().equals(";")){
                 Token t = tokens.getTokenAtual();
                 throw  new Exception("ERRO esperava \";\" em <repeticao> em linha "
                         + t.getLinha() + ", coluna " + t.getColuna() + " (token: '" + t.getValor() + "') ");
             }
             lerProxSeguro("Repeticao");  // consume ";"
             for2();
             if(!tokens.getTokenAtual().getValor().equals(";")){
                 Token t = tokens.getTokenAtual();
                 throw  new Exception("ERRO esperava \";\" em <repeticao> em linha "
                         + t.getLinha() + ", coluna " + t.getColuna() + " (token: '" + t.getValor() + "') ");
             }
             lerProxSeguro("Repeticao");  // consume ";"
             for3();
             if(!tokens.getTokenAtual().getValor().equals(")")){
                 Token t = tokens.getTokenAtual();
                 throw  new Exception("ERRO esperava \")\" em <repeticao> em linha "
                         + t.getLinha() + ", coluna " + t.getColuna() + " (token: '" + t.getValor() + "') ");
             }
             lerProxSeguro("Repeticao");  // consume ")"
             bloco();
             return;
         }
     }
     public void for1() throws Exception {
        if (tokens.getTokenAtual().getValor().equals(";")){
            return;
        }
        if(tipo()){
            tokens.lerProx();
            listaDeclaracao();
            return;
        }
        if(tokens.getTokenAtual().getTipo().equals(Tipo.IDENTIFICADOR)){
            tokens.lerProx();
            if(tokens.getTokenAtual().getValor().equals("=")){
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
        if (tokens.getTokenAtual().getValor().equals(";")){
            return;
        }
        expressao();
    }
     public void for3() throws Exception {
         if (tokens.getTokenAtual().getValor().equals(")")){
             return;
         }
         if(tokens.getTokenAtual().getTipo().equals(Tipo.IDENTIFICADOR)){
             tokens.lerProx();
             if(tokens.getTokenAtual().getValor().equals("=")){
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
        if(tokens.getTokenAtual().getTipo().equals(Tipo.OPERADOR_LOGICO)){
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
        return tokens.getTokenAtual().getValor().equals("+") ||
                tokens.getTokenAtual().getValor().equals("-");
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
        return tokens.getTokenAtual().getValor().equals("*") ||
                tokens.getTokenAtual().getValor().equals("/");
    }

    public void fator() throws Exception {
         var atual = tokens.getTokenAtual();

         if(atual.getTipo().equals(Tipo.IDENTIFICADOR)||
                 atual.getTipo().equals(Tipo.NUMERO)||
                 atual.getTipo().equals(Tipo.LITERAL)||
                 atual.getValor().equals("False")||
                 atual.getValor().equals("True")){
             lerProxSeguro("Fator");  // consume terminal
             return;
         } else if (atual.getValor().equals("(")) {
             lerProxSeguro("Fator");  // consume "("
             expressao();
             if (!tokens.getTokenAtual().getValor().equals(")")){
                 Token t = tokens.getTokenAtual();
                 throw  new Exception("ERRO esperava \")\" em <fator> em linha "
                         + t.getLinha() + ", coluna " + t.getColuna() + " (token: '" + t.getValor() + "') ");

             }
             lerProxSeguro("Fator");  // consume ")"
             return;
         }
         Token t = tokens.getTokenAtual();
         throw  new Exception("ERRO expressao invalida em linha "
                  + t.getLinha() + ", coluna " + t.getColuna() + " (token: '" + t.getValor() + "') ");



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
