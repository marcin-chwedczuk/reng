package pl.marcinchwedczuk.reng.parser;

public enum RTokenType {
    // Parentheses e.g. (foo)
    LPAREN,
    RPAREN,

    // Groups e.g. [0-9]
    LGROUP,
    RGROUP,

    // Ranges e.g. {1,2}
    LRANGE,
    RRANGE,

    STAR,
    PLUS,
    QMARK,
    ALTERNATIVE,

    AT_BEGINNING,
    AT_END,

    CHARACTER,

    // End of input
    EOF
}
