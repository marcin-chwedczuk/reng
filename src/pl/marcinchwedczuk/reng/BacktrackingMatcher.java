package pl.marcinchwedczuk.reng;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("SimplifiableConditionalExpression")
public class BacktrackingMatcher {
    public static Match match(String s, RAst regex) {
        Input input = Input.of(s);

        while (true) {
            int startIndex = input.currentPos();
            AtomicInteger endIndex = new AtomicInteger(0);

            boolean hasMatch = match(input, regex, () -> {
                endIndex.set(input.currentPos());
                return true;
            });

            if (hasMatch) {
                return new Match(s, hasMatch, startIndex, endIndex.get());
            }

            // We are at the end of the input - no match
            if (input.atEnd()) return new Match(s, hasMatch, -1, -1);

            // Try to match from next index
            input.advance(1);
        }
    }

    @SuppressWarnings("SimplifiableConditionalExpression")
    public static boolean match(Input input, RAst ast, Cont cont) {
        RAstType type = ast.type;
        InputPositionMarker m = null;

        switch (type) {
            case AT_BEGINNING:
                return input.atBeginning()
                    ? cont.run()
                    : false;

            case AT_END:
                return input.atEnd()
                    ? cont.run()
                    : false;

            case GROUP:
                if (input.atEnd()) return false;
                if (ast.chars.contains(input.current())) {
                    m = input.markPosition();
                    input.advance(1);
                    try {
                        return cont.run();
                    } finally {
                        input.restorePosition(m);
                    }
                }
                return false;

            case NEGATED_GROUP:
                if (input.atEnd()) return false;
                if (!ast.chars.contains(input.current())) {
                    m = input.markPosition();
                    input.advance(1);
                    try {
                        return cont.run();
                    }
                    finally {
                        input.restorePosition(m);
                    }
                }
                return false;

            case CONCAT:
                return concatRec(input, ast.exprs, 0, cont);

            case ALTERNATIVE:
                return alternativeRec(input, ast.exprs, 0, cont);

            case REPEAT:
                return repeatRec(input, ast, 0, cont);

            default: throw new AssertionError("Unknown enum value: " + type);
        }
    }

    private static boolean concatRec(Input input,
                                     List<RAst> exprs,
                                     int currExpr,
                                     Cont cont) {
        if (currExpr == exprs.size()) {
            return cont.run();
        }

        // Match exprs.get(currExpr)
        return match(input, exprs.get(currExpr), () ->
            // If it succeeded then match next expression
            concatRec(input, exprs, currExpr + 1, cont)
        );
    }

    private static boolean repeatRec(Input input,
                                     RAst repeatAst,
                                     long matchCount,
                                     Cont cont) {
        InputPositionMarker positionBeforeMatch = input.markPosition();

        boolean matched = match(input, repeatAst.headExpr(), () -> {
            if ((matchCount+1) > repeatAst.repeatMax)
                return false;

            // We must be careful when matching empty string inside * or + operator.
            // If the matched input is "" we can have as many repeats as we want.
            InputPositionMarker positionAfterMatch = input.markPosition();
            if (positionAfterMatch.equals(positionBeforeMatch)) {
                return cont.run();
            } else {
                return repeatRec(input, repeatAst, matchCount + 1, cont);
            }
        });

        if (!matched && (matchCount >= repeatAst.repeatMin)) {
            // r{N} did not match.
            // Here we are matching r{N-1}, we are sure it is matching
            // because this function was called.
            return cont.run();
        }

        // r{N} matched?
        return matched;
    }

    private static boolean alternativeRec(Input input,
                                          List<RAst> expr,
                                          int currExpr,
                                          Cont cont) {
        if (currExpr == expr.size()) {
            // We tried all alternatives but achieved no match.
            return false;
        }

        boolean matched = match(input, expr.get(currExpr), cont);
        if (matched) return true;

        // Let's try next alternative "branch"
        return alternativeRec(input, expr, currExpr+1, cont);
    }
}
