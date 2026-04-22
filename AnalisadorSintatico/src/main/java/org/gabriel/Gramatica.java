package org.gabriel;


public class Gramatica {
    Tokens  tokens;
    public Gramatica(Tokens tokens) {
        this.tokens = tokens;
    }
    public void programa() throws Exception {
        bloco();
    }


    public void bloco() throws Exception{
        if (!this.tokens.getTokenAtual().getValor().equals("{")){
            throw new Exception("Falta {");
        }
        this.tokens.LerProx();
        listaComandos();

        if (!this.tokens.getTokenAtual().getValor().equals("}")){
            throw new Exception("Falta }");
        }

    }
    public void listaComandos() throws Exception {

        if (tokens.getTokenAtual().getValor().equals("}")) {
            return;
        }
        // ListaComandos -> Comando ListaComandos
        if (ehInicioComando(tokens.getTokenAtual())) {
            comando();
            listaComandos();
            return;
        }

        throw new Exception("Token inesperado em <ListaComandos>: " + tokens.getTokenAtual().getValor());
    }

    private boolean ehInicioComando(Token token) {
        return token.getTipo() == Tipo.IDENTIFICADOR
                || token.getValor().equals("if")
                || token.getValor().equals("while")
                || token.getValor().equals("{")
                || token.getValor().equals("int")
                || token.getValor().equals("float")
                || token.getValor().equals("char")
                || token.getValor().equals("bool")
                || token.getValor().equals(";");
    }

    public void comando() throws Exception {
        tokens.LerProx();
        if (tokens.getTokenAtual().getValor().equals(";")) {return;}

        else if(tokens.getTokenAtual().getTipo().equals(Tipo.IDENTIFICADOR)) {
            atribuicao();
            return;
        }

        else if(tokens.getTokenAtual().getValor().equals("if")){
            condicao();
            return;
        }

        else if(tokens.getTokenAtual().getValor().equals("while")){
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
        throw new Exception("Token inesperado em <ListaComandos>: " + tokens.getTokenAtual().getValor());

    }
    public void declaracao() throws Exception {
        if (!tipo()) {throw  new Exception("ERRO devia ser um tipo valido");}
        tokens.LerProx();
        if (!tokens.getTokenAtual().getTipo().equals(Tipo.IDENTIFICADOR)) {throw new Exception("ERRO devia ser um identificador");}
    }
    public boolean tipo() throws Exception {return true;}
    public void atribuicao() throws Exception {}
    public void condicao() throws Exception {}
    public void senao() throws Exception {}
    public void repeticao() throws Exception {}
    public void expressao() throws Exception {}
    public void operador() throws Exception {}
    public void fator() throws Exception {}
}
/*
<Programa> ::= <Bloco>

<Bloco> ::= "{" <ListaComandos> "}"

<ListaComandos> ::= <Comando> <ListaComandos> | ε

<Comando> ::= <Atribuicao> | <Condicao> | <Repeticao> | <Bloco> | <Declaracao> | ";"

<Declaracao> ::= <Tipo> IDENTIFICADOR ";"

<Tipo> ::= "int" | "float" | "char" | "bool"

<Atribuicao> ::= IDENTIFICADOR "=" <Expressao> ";"

<Condicao> ::= "if" "(" <Expressao> ")" <Bloco> <Senao>

<Senao> ::= "else" <Bloco> | ε

<Repeticao> ::= "while" "(" <Expressao> ")" <Bloco>

<Expressao> ::= <Fator> <Operador> <Fator>

<Operador> ::= OPERADOR_LOGICO | OPERADOR_ARITMETICO

<Fator> ::= IDENTIFICADOR | NUMERO | LITERAL | "true" | "false" | "(" <Expressao> ")" | "!" <Fator> | "-" <Fator>
 */