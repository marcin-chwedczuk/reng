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
        assertMatches("a", rAtEnd);
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

    @Test public void match_anchors() {
        // regex: ^abc$
        RAst rAbcAlone = RAst.concat(
                RAst.atBeginning(),
                RAst.literal("abc"),
                RAst.atEnd());

        assertMatches("abc", rAbcAlone);

        assertNotMatches("abcx", rAbcAlone);
        assertNotMatches("xabc", rAbcAlone);
        assertNotMatches("ac", rAbcAlone);
    }

    @Test public void match_within_input() {
        RAst rA = RAst.group('a');

        assertMatches("axxx", rA);
        assertMatches("xxxa", rA);
        assertMatches("xaxx", rA);
        assertMatches("xxax", rA);
    }

    @Test
    public void match_star_with_alternative() {
        // regex: ^(A|B)*$
        RAst rABs = RAst.fullMatch(
            RAst.star(
                RAst.alternative(
                        RAst.group('A'),
                        RAst.group('B'))));

        assertMatches("", rABs);
        assertMatches("AA", rABs);
        assertMatches("ABAB", rABs);
        assertMatches("ABABA", rABs);

        assertNotMatches("x", rABs);
        assertNotMatches("AxA", rABs);
    }

    @Test public void match_star_with_concatenation() {
        // Regex: ^(foo)*$
        RAst rFooStar = RAst.fullMatch(
                RAst.star(RAst.literal("foo")));

        assertMatches("", rFooStar);
        assertMatches("foo", rFooStar);
        assertMatches("foofoo", rFooStar);

        assertNotMatches("fo", rFooStar);
        assertNotMatches("foofox", rFooStar);
    }

    @Test public void match_alternative_with_concatenation() {
        // Regex: ^a(foo|bar)z$
        RAst rFooStar = RAst.fullMatch(
                RAst.concat(
                        RAst.group('a'),
                        RAst.alternative(
                                RAst.literal("foo"), RAst.literal("bar")),
                        RAst.group('z')));

        assertMatches("afooz", rFooStar);
        assertMatches("abarz", rFooStar);

        assertNotMatches("", rFooStar);
        assertNotMatches("az", rFooStar);
        assertNotMatches("afoo", rFooStar);
        assertNotMatches("fooz", rFooStar);
        assertNotMatches("afoobarz", rFooStar);
    }

    private static void assertMatches(String input, RAst regex) {
        Match m = BacktrackingMatcher.match(input, regex);

        assertTrue(
            "Regex " + regex + " should match '" + input + "'.",
            m.hasMatch);
    }

    private static void assertNotMatches(String input, RAst regex) {
        Match m = BacktrackingMatcher.match(input, regex);

        assertFalse(
                "Regex " + regex + " should NOT match '" + input + "'.",
                m.hasMatch);
    }
}
