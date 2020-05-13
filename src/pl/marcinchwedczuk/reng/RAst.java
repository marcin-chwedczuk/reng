package pl.marcinchwedczuk.reng;

import java.util.*;

import static java.util.Collections.*;
import static java.util.stream.Collectors.joining;

public class RAst {

    public final RAstType type;
    public final Set<Character> chars;
    public final List<RAst> exprs;

    // Repeat from to, both inclusive
    public final long repeatMin;
    public final long repeatMax;

    public RAst(RAstType type,
                Set<Character> chars,
                List<RAst> exprs,
                long repeatMin,
                long repeatMax) {
        this.type = type;
        this.chars = chars;
        this.exprs = exprs;
        this.repeatMin = repeatMin;
        this.repeatMax = repeatMax;
    }

    public RAst(RAstType type,
                 Set<Character> chars) {
        this(type,
             chars,
             Collections.emptyList(),
             -1, -1);
    }

    public RAst(RAstType type,
                 List<RAst> exprs) {
        this(type,
             Collections.emptySet(),
             exprs,
             -1, -1);
    }

    public RAst headExpr() {
        return exprs.iterator().next();
    }

    @Override
    public String toString() {
        return toString(-1);
    }

    private String toString(int outsidePriority) {
        String tmp = null;

        switch (type) {
            case GROUP:
                if (chars.size() == 1) {
                    tmp = chars
                            .iterator().next()
                            .toString();
                }
                else {
                    tmp = chars.stream()
                        .map(Object::toString)
                        .collect(joining("", "[", "]"));
                }
                break;

            case INVERTED_GROUP:
                tmp = chars.stream()
                        .map(Object::toString)
                        .collect(joining("", "[^", "]"));
                break;

            case REPEAT:
                tmp = headExpr().toString(RAstType.REPEAT.priority) + "*";
                break;

            case CONCAT:
                tmp = exprs.stream()
                        .map(e -> e.toString(RAstType.CONCAT.priority))
                        .collect(joining());
                break;

            case ALTERNATIVE:
                tmp = exprs.stream()
                        .map(e -> e.toString(RAstType.ALTERNATIVE.priority))
                        .collect(joining("|"));
                break;

            case AT_BEGINNING: tmp = "^"; break;
            case AT_END: tmp = "$"; break;

            default: throw new AssertionError("Unknown enum value: " + type);
        }

        return (outsidePriority > type.priority)
                ? addParents(tmp)
                : tmp;
    }

    private static String addParents(String s) {
        return "(" + s + ")";
    }

    public static RAst group(char... chars) {
        return new RAst(
            RAstType.GROUP, toSet(chars));
    }

    public static RAst invGroup(char... chars) {
        return new RAst(
            RAstType.INVERTED_GROUP, toSet(chars));
    }

    public static RAst concat(RAst... exprs) {
        return new RAst(
            RAstType.CONCAT,
            Arrays.asList(exprs));
    }

    public static RAst literal(String s) {
        RAst[] chars = s.chars()
                .mapToObj(c -> RAst.group((char) c))
                .toArray(RAst[]::new);

        return RAst.concat(chars);
    }

    public static RAst alternative(RAst... expr) {
        return new RAst(
            RAstType.ALTERNATIVE,
            Arrays.asList(expr));
    }

    public static RAst star(RAst expr) {
        return repeat(expr, 0, Long.MAX_VALUE);
    }

    public static RAst repeat(RAst expr, long min, long max) {
        return new RAst(
                RAstType.REPEAT,
                emptySet(),
                singletonList(expr),
                min, max);
    }

    public static RAst atBeginning() {
        return new RAst(
                RAstType.AT_BEGINNING,
                emptySet(),
                emptyList(),
                -1, -1);
    }

    public static RAst atEnd() {
        return new RAst(
                RAstType.AT_END,
                emptySet(),
                emptyList(),
                -1, -1);
    }

    public static RAst fullMatch(RAst r) {
        return RAst.concat(
                atBeginning(),
                r,
                atEnd());
    }

    private static Set<Character> toSet(char[] chars) {
        Set<Character> set = new HashSet<>();

        for(char c: chars) {
            set.add(c);
        }

        return set;
    }
}
