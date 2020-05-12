package pl.marcinchwedczuk.reng;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
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

            // We checked empty string - no match
            if (input.atEnd()) return new Match(s, hasMatch, -1, -1);

            // Try to match from next index
            input.advance(1);
        }
    }

    public static boolean match(Input input, RAst regex, Cont cont) {
        RAstType type = regex.type;
        InputMarker m = null;

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
                // TODO: Handle empty groups
                if (input.atEnd()) return false;
                if (regex.chars.contains(input.current())) {
                    m = input.mark();
                    input.advance(1);
                    try {
                        return cont.run();
                    } finally {
                        input.goTo(m);
                    }
                }
                return false;

            case INVERTED_GROUP:
                if (input.atEnd()) return false;
                if (!regex.chars.contains(input.current())) {
                    m = input.mark();
                    input.advance(1);
                    try {
                        return cont.run();
                    }
                    finally {
                        input.goTo(m);
                    }
                }
                return false;

            case CONCAT:
                return concatRec(input, regex.exprs, 0, cont);

            case ALTERNATIVE:
                return alternativeRec(input, regex.exprs, 0, cont);

            case STAR:
                return starRec(input, regex.headExpr(), cont);

            default: throw new AssertionError("Unknown enum value: " + type);
        }
    }

    private static boolean concatRec(Input input,
                                  List<RAst> exprs,
                                  int curr,
                                  Cont cont) {
        if (curr == exprs.size()) {
            return cont.run();
        }

        return match(input, exprs.get(curr), () ->
            concatRec(input, exprs, curr + 1, cont)
        );
    }

    private static boolean starRec(Input input,
                                RAst r,
                                Cont cont) {
        boolean b = match(input, r, () ->
            starRec(input, r, cont)
        );

        if (!b) {
            // r{N} does not match.
            // Here  we are matching r{N-1}, we are sure it is matching
            // because this function was called.
            return cont.run();
        }

        // r{N} matched.
        return b;
    }

    private static boolean alternativeRec(Input input,
                                          List<RAst> expr,
                                          int curr,
                                          Cont cont) {
        if (curr == expr.size()) {
            // We tried all alternatives but achieved no match.
            return false;
        }

        boolean b = match(input, expr.get(curr), cont);
        if (!b) {
            // Let's try next alternative branch
            return alternativeRec(input, expr, curr+1, cont);
        }

        return true;
    }
}
