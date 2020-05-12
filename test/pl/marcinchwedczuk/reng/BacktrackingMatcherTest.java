package pl.marcinchwedczuk.reng;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BacktrackingMatcherTest {
    @Test public void match_group() {
        RAst rAbc = RAst.group('a', 'b', 'c');

        assertMatches("a", rAbc);
        assertMatches("abc", rAbc);

        assertNotMatches("x", rAbc);
    }

    @Test public void match_inverted_group() {
        RAst rNotAbc = RAst.invGroup('a', 'b', 'c');

        assertNotMatches("a", rNotAbc);
        assertNotMatches("abc", rNotAbc);

        assertMatches("x", rNotAbc);
        assertMatches("xyz", rNotAbc);
    }

    @Test public void match_at_begining() {
       RAst rAtBeginning = RAst.atBeginning();

       assertMatches("", rAtBeginning);
       assertMatches("a", rAtBeginning);
    }

    @Test public void match_at_end() {
        RAst rAtEnd = RAst.atEnd();

        assertMatches("", rAtEnd);
        assertNotMatches("a", rAtEnd);
    }

    @Test public void match_concat() {
        RAst rAbc = RAst.concat(
                RAst.group('a'),
                RAst.group('b'),
                RAst.group('c')
        );

        assertMatches("abc", rAbc);
        assertMatches("abcDef", rAbc);

        assertNotMatches("", rAbc);
        assertNotMatches("a", rAbc);
        assertNotMatches("ab", rAbc);
        assertNotMatches("abx", rAbc);
        assertNotMatches("xbc", rAbc);
    }

    @Test public void match_alternative() {
        RAst rAltAbc = RAst.alternative(
                RAst.group('a'),
                RAst.group('b'),
                RAst.group('c')
        );

        assertMatches("a", rAltAbc);
        assertMatches("b", rAltAbc);
        assertMatches("c", rAltAbc);
        assertMatches("axx", rAltAbc);

        assertNotMatches("", rAltAbc);
        assertNotMatches("x", rAltAbc);
    }

    @Test public void match_star() {
        RAst rAStar = RAst.star(RAst.group('a'));

        assertMatches("", rAStar);
        assertMatches("a", rAStar);
        assertMatches("aa", rAStar);
        assertMatches("aaa", rAStar);
        assertMatches("aaax", rAStar);
        assertMatches("x", rAStar);
    }

    private static void assertMatches(String input, RAst regex) {
        boolean m = BacktrackingMatcher.match(
                Input.of(input),
                regex);

        assertTrue(
            "Regex " + regex + " should match '" + input + "'.",
            m);
    }

    private static void assertNotMatches(String input, RAst regex) {
        boolean m = BacktrackingMatcher.match(
                Input.of(input),
                regex);

        assertFalse(
                "Regex " + regex + " should NOT match '" + input + "'.",
                m);
    }
}
