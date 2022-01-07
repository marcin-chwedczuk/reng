package pl.marcinchwedczuk.reng;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BacktrackingMatcherTest {
    @Test public void matches_group() {
        RAst rAbc = RAst.group('a', 'b', 'c');

        assertMatches("a", rAbc);
        assertMatches("b", rAbc);
        assertMatches("c", rAbc);
        assertMatches("abc", rAbc);

        assertNotMatches("", rAbc);
        assertNotMatches("d", rAbc);
    }

    @Test public void matches_inverted_group() {
        RAst rNotAbc = RAst.invGroup('a', 'b', 'c');

        assertMatches("d", rNotAbc);
        assertMatches("xyz", rNotAbc);

        assertNotMatches("", rNotAbc);
        assertNotMatches("a", rNotAbc);
        assertNotMatches("b", rNotAbc);
        assertNotMatches("c", rNotAbc);
        assertNotMatches("abc", rNotAbc);
    }

    @Test public void matches_at_beginning() {
       RAst rAtBeginning = RAst.atBeginning();

       // Every string has a beginning
       assertMatches("", rAtBeginning);
       assertMatches("a", rAtBeginning);
        assertMatches("abc", rAtBeginning);
    }

    @Test public void matches_at_end() {
        RAst rAtEnd = RAst.atEnd();

        // Every string has an ending
        assertMatches("", rAtEnd);
        assertMatches("a", rAtEnd);
        assertMatches("abc", rAtEnd);
    }

    @Test public void matches_concat() {
        RAst rAbc = RAst.concat(
                RAst.group('a'),
                RAst.group('b'),
                RAst.group('c')
        );

        assertMatches("abc", rAbc);
        assertMatches("abcDef", rAbc);
        assertMatches("XYZabc", rAbc);
        assertMatches("+abc+", rAbc);

        assertNotMatches("", rAbc);
        assertNotMatches("a", rAbc);
        assertNotMatches("ab", rAbc);
        assertNotMatches("abx", rAbc);
        assertNotMatches("xbc", rAbc);
        assertNotMatches("xxxxxxx", rAbc);
    }

    @Test public void matches_alternative() {
        RAst rAltAbc = RAst.alternative(
                RAst.group('a'),
                RAst.group('b'),
                RAst.group('c')
        );

        assertMatches("a", rAltAbc);
        assertMatches("b", rAltAbc);
        assertMatches("c", rAltAbc);
        assertMatches("axx", rAltAbc);
        assertMatches("xbx", rAltAbc);

        assertNotMatches("", rAltAbc);
        assertNotMatches("x", rAltAbc);
        assertNotMatches("xyz", rAltAbc);
    }

    @Test public void matches_star() {
        RAst rAStar = RAst.star(RAst.group('a'));

        assertMatches("", rAStar);
        assertMatches("a", rAStar);
        assertMatches("aa", rAStar);
        assertMatches("aaa", rAStar);
        assertMatches("aaax", rAStar);

        // Empty match
        assertMatches("x", rAStar);
        assertMatches("xyz", rAStar);
    }

    @Test public void matches_plus() {
        RAst rAStar = RAst.plus(RAst.group('a'));

        assertMatches("a", rAStar);
        assertMatches("aa", rAStar);
        assertMatches("aaa", rAStar);
        assertMatches("aaax", rAStar);

        assertNotMatches("", rAStar);
        assertNotMatches("x", rAStar);
        assertNotMatches("xyz", rAStar);
    }

    @Test public void matches_repetition() {
        RAst rA23 = RAst.repeat(RAst.group('a'), 2, 3);

        assertNotMatches("", rA23);
        assertNotMatches("a", rA23);
        assertMatches("aa", rA23);
        assertMatches("aaa", rA23);

        // Matches a substring aaa
        assertMatches("aaaa", rA23);
    }

    @Test public void matches_anchors() {
        // regex: ^abc$
        RAst rAbcAlone = RAst.concat(
                RAst.atBeginning(),
                RAst.literal("abc"),
                RAst.atEnd());

        assertMatches("abc", rAbcAlone);

        assertNotMatches("", rAbcAlone);
        assertNotMatches("abcx", rAbcAlone);
        assertNotMatches("xabc", rAbcAlone);
        assertNotMatches("xabcx", rAbcAlone);
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

    @Test public void match_repeat_with_min_and_max() {
        // regex: ^a{1,3}$
        RAst r = RAst.fullMatch(
                RAst.repeat(RAst.group('a'), 1, 3));

        assertMatches("a", r);
        assertMatches("aa", r);
        assertMatches("aaa", r);

        assertNotMatches("", r);
        assertNotMatches("aaaa", r);
        assertNotMatches("aaaaaa", r);
    }

    @Test public void repeat_does_gready_match() {
        String input = "aaaaab";
        RAst r = RAst.repeat(RAst.group('a'), 1, 5);

        Match m = BacktrackingMatcher.match(input, r);

        Assert.assertEquals("aaaaa", m.matched());
    }

    @Test public void bug_repeatedly_match_empty_string() {
        // regex (b|a*)+
        String input = "bbc";
        RAst r = RAst.plus(
                RAst.alternative(
                    RAst.group('b'),
                    RAst.star(RAst.group('a'))
                    ));

        Match m = BacktrackingMatcher.match(input, r);
        Assert.assertEquals("bb", m.matched());
    }

    @Test public void bug_astar_matches_anything() {
        // regex (b|a*)+
        String input = "bbc";
        RAst r = RAst.star(RAst.group('a'));

        Match m = BacktrackingMatcher.match(input, r);
        Assert.assertEquals("", m.matched());
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
