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
            Token t = this.tokens.getTokenAtual();
            throw new Exception("ERRO Falta { em linha " + t.getLinha() + ", coluna " + t.getColuna() + " (token: '" + t.getValor() + "')");
        }
        this.tokens.LerProx();
        listaComandos();

        if (!this.tokens.getTokenAtual().getValor().equals("}")){
            Token t = this.tokens.getTokenAtual();
            throw new Exception("ERRO Falta } em linha " + t.getLinha() + ", coluna " + t.getColuna() + " (token: '" + t.getValor() + "')");
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

        Token t = tokens.getTokenAtual();
        throw new Exception("ERRO Token inesperado em <ListaComandos>: '" + t.getValor() + "' em linha " + t.getLinha() + ", coluna " + t.getColuna());
    }

    private boolean ehInicioComando(Token token) throws Exception {
        return token.getTipo() == Tipo.IDENTIFICADOR
                || token.getValor().equals("if")
                || token.getValor().equals("while")
                || token.getValor().equals("{")
                || tipo()
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
        Token t = tokens.getTokenAtual();
        throw new Exception("ERRO Token inesperado em <Comando>: '" + t.getValor() + "' em linha " + t.getLinha() + ", coluna " + t.getColuna());

    }
    public void declaracao() throws Exception {
        if (!tipo()) {
            Token t = tokens.getTokenAtual();
            throw new Exception("ERRO esperava tipo em <Declaracao> em linha " + t.getLinha() + ", coluna " + t.getColuna() + " (token: '" + t.getValor() + "')");
        }
        tokens.LerProx();
        if (!tokens.getTokenAtual().getTipo().equals(Tipo.IDENTIFICADOR)) {
            Token t = tokens.getTokenAtual();
            throw new Exception("ERRO esperava identificador em <Declaracao> em linha " + t.getLinha() + ", coluna " + t.getColuna() + " (token: '" + t.getValor() + "')");
        }
        tokens.LerProx();
        if (!tokens.getTokenAtual().getValor().equals(";")) {
            Token t = tokens.getTokenAtual();
            throw new Exception("ERRO esperava ; em <Declaracao> em linha " + t.getLinha() + ", coluna " + t.getColuna() + " (token: '" + t.getValor() + "')");
        }
    }

    public boolean tipo() throws Exception {
        var valor = tokens.getTokenAtual().getValor();
        return valor.equals("int") || valor.equals("float") || valor.equals("char") || valor.equals("bool");
    }
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