package pl.marcinchwedczuk.reng.parser;

public enum RTokenType {
    // Parentheses e.g. (foo)
    LPAREN,
    RPAREN,

    // Character Groups e.g. [0-9]
    LGROUP,
    RGROUP,

    // Ranges e.g. {1,2}
    LRANGE,
    RRANGE,

    // Metacharacters: . * + ? |
    MATCH_ANY,
    STAR,
    PLUS,
    QMARK,
    ALTERNATIVE,

    // Anchors: ^ and $
    AT_BEGINNING,
    AT_END,

    CHARACTER,

    // End of input
    EOF
}
