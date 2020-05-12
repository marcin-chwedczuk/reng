package pl.marcinchwedczuk.reng.parser;

import pl.marcinchwedczuk.reng.RAst;

import java.util.ArrayList;
import java.util.List;

public class RParser {
    public static RAst parse(String s) {
        List<RToken> tokens = new RLexer(s).split();
        RParser parser =  new RParser(tokens);
        return parser.parse();
    }

    private final List<RToken> tokens;
    private int curr;

    private RToken currToken() {
        return tokens.get(curr);
    }

    private void expectCurrToken(RTokenType type) {
        if(currToken().type != type) {
            throw new RParseException(currToken().pos,
                "Unexpected input: expected " + type +
                        "got " + currToken().type + ".");
        }
    }

    private RToken consume(RTokenType type) {
        RToken t = currToken();
        expectCurrToken(type);
        if (curr < tokens.size()) curr++;
        return t;
    }

    private void consume(char c) {
        RToken t = consume(RTokenType.CHARACTER);
        if (t.c != c) {
            throw new RParseException(t.pos,
                "Expected '" + c + "' but got '" + t.c + "'.");
        }
    }

    private boolean consumeIfPresent(RTokenType type) {
        if (currToken().type == type) {
            consume(type);
            return true;
        }

        return false;
    }

    private boolean consumeIfPresent(char c) {
        if (currToken().type == RTokenType.CHARACTER &&
            currToken().c == c)
        {
            consume(RTokenType.CHARACTER);
            return true;
        }

        return false;
    }

    private boolean lookahead(int ntokens, RTokenType type) {
        if ((curr + ntokens) < tokens.size()) {
            RToken t = tokens.get(curr + ntokens);
            return t.type == type;
        }

        return false;
    }

    private boolean lookahead(int ntokens, char c) {
        if ((curr + ntokens) < tokens.size()) {
            RToken t = tokens.get(curr + ntokens);
            return t.type == RTokenType.CHARACTER &&
                    t.c == c;
        }

        return false;
    }

    private RParser(List<RToken> tokens) {
        this.tokens = tokens;
        this.curr = 0;
    }

    public RAst parse() {
        RAst ast = Gregex();
        consume(RTokenType.EOF);
        return ast;
    }

    public RAst Gregex() {
        return Galternative();
    }

    private RAst Galternative() {
        List<RAst> alternatives = new ArrayList<>();

        alternatives.add(Gconcat());

        while (lookahead(0, RTokenType.ALTERNATIVE)) {
            consume(RTokenType.ALTERNATIVE);
            alternatives.add(Gconcat());
        }

        return RAst.alternative(alternatives.toArray(new RAst[0]));
    }

    private RAst Gconcat() {
        List<RAst> exprs = new ArrayList<>();

        exprs.add(Gmultiplicative());

        while (!lookahead(0, RTokenType.ALTERNATIVE) &&
                !lookahead(0, RTokenType.RPAREN) &&
                !lookahead(0, RTokenType.EOF)) {
            exprs.add(Gmultiplicative());
        }

        return RAst.concat(exprs.toArray(new RAst[0]));
    }

    private RAst Gmultiplicative() {
        RAst term = Gterm();

        while(lookahead(0, RTokenType.STAR) ||
            lookahead(0, RTokenType.PLUS)   ||
            lookahead(0, RTokenType.QMARK)  ||
            lookahead(0, RTokenType.LRANGE))
        {
            if (lookahead(0, RTokenType.STAR)) {
                consume(RTokenType.STAR);
                term = RAst.star(term);
            }
            else if (lookahead(0, RTokenType.PLUS)) {
                consume(RTokenType.PLUS);
                // a+ = aa*
                term = RAst.concat(term, RAst.star(term));
            }
            else if (lookahead(0, RTokenType.QMARK)) {
                // a? = (a | )
                // TODO: Move to generic range RAst.repeat(from: 1, to: Inf, r)
                throw new RuntimeException("Currently not supported");
            }
            else {
                // TODO: Implement ranges - Grange - should return tuple
                // (from, to) - both long's.
                throw new RuntimeException("Currently not supported");
            }
        }

        return term;
    }

    private RAst Gterm() {
        // Term:
        // - Character
        // - Group
        // - Anchor (^ and $)
        // - Match any
        // - ( regex )

        if (lookahead(0, RTokenType.AT_BEGINNING)) {
            consume(RTokenType.AT_BEGINNING);
            return RAst.atBeginning();
        }
        else if (lookahead(0, RTokenType.AT_END)) {
            consume(RTokenType.AT_END);
            return RAst.atEnd();
        }
        else if (lookahead(0, RTokenType.LGROUP)) {
            return Ggroup();
        }
        else if (lookahead(0, RTokenType.CHARACTER)) {
            return RAst.group(Gchar());
        }
        else if (lookahead(0, RTokenType.LPAREN)) {
            consume(RTokenType.LPAREN);
            RAst tmp = Gregex();
            consume(RTokenType.RPAREN);
            return tmp;
        }
        else {
            throw new RParseException(currToken().pos,
                    "Unexpected token " + currToken().type + ".");
        }
    }

    private RAst Ggroup() {
        consume(RTokenType.LGROUP);

        boolean negated = consumeIfPresent('^');
        CharList chars = new CharList();

        while (true) {
            if (lookahead(0, RTokenType.EOF)) {
                throw new RParseException(currToken().pos,
                        "Unexpected end of input inside [...].");
            }

            if (lookahead(0, RTokenType.RGROUP)) {
               if (chars.size() == 0) {
                   throw new RParseException(currToken().pos,
                           "Empty groups are not supported, " +
                           "use non empty group like '[abc]'.");
               }

               return negated
                       ? RAst.invGroup(chars.toArray())
                       : RAst.group(chars.toArray());
            }
            else if (lookahead(0, RTokenType.CHARACTER) &&
                lookahead(1, '-') &&
                lookahead(2, RTokenType.CHARACTER)) {

                chars.addAll(Grange());
            }
            else {
                chars.add(Gchar());
            }
        }
    }

    private char[] Grange() {
        // e.g. 0-9 inside [ ]
        RToken tFrom = consume(RTokenType.CHARACTER);
        consume('-');
        RToken tTo = consume(RTokenType.CHARACTER);

        // TODO: Add validation e.g. tTo < tFrom

        // TODO: Intro more efficient AST type or assume ASCII input
        char[] chars = new char[tTo.c - tFrom.c + 1];

        for (int c = tFrom.c; c <= tTo.c; c++) {
            chars[c - tFrom.c] = (char)c;
        }

        return chars;
    }

    private char Gchar() {
        RToken t = consume(RTokenType.CHARACTER);
        return t.c;
    }
}