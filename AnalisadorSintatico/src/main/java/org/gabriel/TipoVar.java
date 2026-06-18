package org.gabriel;

public enum TipoVar {
    INT, FLOAT, CHAR, BOOL;

    /**
     * Tabela de compatibilidade de tipos para operações aritméticas/lógicas.
     *
     *          int     float   char    bool
     * int  →   int     float   ERRO    ERRO
     * float→   float   float   ERRO    ERRO
     * char →   ERRO    ERRO    char    ERRO
     * bool →   ERRO    ERRO    ERRO    bool
     *
     */
    public static TipoVar compativel(TipoVar a, TipoVar b) {
        if (a == null || b == null) return null;
        return switch (a) {
            case INT -> switch (b) {
                case INT   -> INT;
                case FLOAT -> FLOAT;
                case CHAR  -> null;
                case BOOL  -> null;
            };
            case FLOAT -> switch (b) {
                case INT, FLOAT -> FLOAT;
                case CHAR, BOOL -> null;
            };
            case CHAR -> switch (b) {
                case  CHAR -> CHAR;
                case INT, FLOAT, BOOL -> null;
            };
            case BOOL -> switch (b) {
                case BOOL  -> BOOL;
                default    -> null;
            };
        };
    }
}
