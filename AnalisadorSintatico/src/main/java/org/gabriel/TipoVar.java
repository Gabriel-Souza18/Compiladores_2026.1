package org.gabriel;

public enum TipoVar {
    INT, FLOAT, CHAR, BOOL;

    /**
     * Tabela de compatibilidade de tipos para operações aritméticas/lógicas.
     *
     *          int     float   char    bool
     * int  →   int     float   char    ERRO
     * float→   float   float   ERRO    ERRO
     * char →   char    ERRO    char    ERRO
     * bool →   ERRO    ERRO    ERRO    bool
     *
     * @return o tipo resultante da operação, ou {@code null} se incompatível (ERRO).
     */
    public static TipoVar compativel(TipoVar a, TipoVar b) {
        if (a == null || b == null) return null;
        return switch (a) {
            case INT -> switch (b) {
                case INT   -> INT;
                case FLOAT -> FLOAT;
                case CHAR  -> CHAR;
                case BOOL  -> null;
            };
            case FLOAT -> switch (b) {
                case INT, FLOAT -> FLOAT;
                case CHAR, BOOL -> null;
            };
            case CHAR -> switch (b) {
                case INT, CHAR -> CHAR;
                case FLOAT, BOOL -> null;
            };
            case BOOL -> switch (b) {
                case BOOL  -> BOOL;
                default    -> null;
            };
        };
    }
}
/*
tabela de conversao de tipos

x       int     float   string      bool
int     int     float   string      ERRO
float           float   string      ERRO
string                  string      ERRO
bool                                bool

* */
