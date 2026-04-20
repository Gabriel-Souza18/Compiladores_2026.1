package org.gabriel;

public class Gramatica {
    public Boolean programa(){return true;}
    public Boolean bloco(){return true;}
    public Boolean listaComandos(){return true;}
    public Boolean comando(){return true;}
    public Boolean declaracao(){return true;}
    public Boolean tipo(){return true;}
    public Boolean atribuicao(){return true;}
    public Boolean condicao(){return true;}
    public Boolean senao(){return true;}
    public Boolean repeticao(){return true;}
    public Boolean expressao(){return true;}
    public Boolean operador(){return true;}
    public Boolean fator(){return true;}
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