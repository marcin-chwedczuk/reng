package pl.marcinchwedczuk.reng;

import org.junit.Test;
import pl.marcinchwedczuk.reng.parser.RParser;

import static org.junit.Assert.*;

public class SmokeTests {
    @Test
    public void parse_double_literal() {
        String r = "^[-+]?(([0-9]+(\\.[0-9]*)?)|(\\.[0-9]+))([Ee]-?[0-9]+)?$";
        RAst ast = RParser.parse(r);

        assertFullMatch("0", ast);
        assertFullMatch("1", ast);
        assertFullMatch("-1", ast);
        assertFullMatch("+1", ast);
        assertFullMatch("123", ast);

        assertFullMatch("3.1415", ast);
        assertFullMatch("3.1415E10", ast);
        assertFullMatch("1.23e-7", ast);
        assertFullMatch("-1.23e7", ast);

        assertFullMatch(".23", ast);
        assertFullMatch("-.23", ast);
        assertFullMatch("-.23E3", ast);

        assertNotMatches("32..3", ast);
        assertNotMatches("--32", ast);
        assertNotMatches("32.+3", ast);
        assertNotMatches(".", ast);
        assertNotMatches(".+", ast);
        assertNotMatches("+.", ast);
        assertNotMatches("E-7", ast);
        assertNotMatches("E7", ast);
        assertNotMatches("1e3E7", ast);
        assertNotMatches("1e", ast);
        assertNotMatches("1e-", ast);
        assertNotMatches("1e+", ast);
        assertNotMatches("1+", ast);
        assertNotMatches("1-", ast);
        assertNotMatches(".1-", ast);
        assertNotMatches("32f4.24", ast);
        assertNotMatches("32+4.24", ast);
    }

    @Test public void simple_email_address() {
        String r = "^[_a-zA-Z][_a-zA-Z0-9]*@gmail\\.com$";
        RAst ast = RParser.parse(r);

        assertFullMatch("foo@gmail.com", ast);
        assertFullMatch("bar123@gmail.com", ast);
        assertFullMatch("foo_bar_1988@gmail.com", ast);
        assertFullMatch("_foo_@gmail.com", ast);

        assertNotMatches("123@gmail.com", ast);
        assertNotMatches("@gmail.com", ast);
        assertNotMatches("foo bar@gmail.com", ast);
        assertNotMatches("fooXgmail.com", ast);
        assertNotMatches("foo@gmailXcom", ast);
    }

    @Test public void almost_valid_date() {
        // Format yyyy-MM-dd
        String r = "^((19|20)[0-9]{2})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$";
        RAst ast = RParser.parse(r);

        assertFullMatch("2000-01-01", ast);
        assertFullMatch("1992-11-31", ast);
        assertFullMatch("2012-12-24", ast);
        assertFullMatch("2012-01-04", ast);
        assertFullMatch("2022-05-14", ast);

        assertNotMatches("1800-03-01", ast);
        assertNotMatches("2000-23-01", ast);
        assertNotMatches("2000-13-01", ast);
        assertNotMatches("2000-03-41", ast);
        assertNotMatches("2000-03-32", ast);
        assertNotMatches("20000302", ast);
        assertNotMatches("2000/03/02", ast);
        assertNotMatches("2000-fo-02", ast);
    }

    private static void assertFullMatch(String input, RAst regex) {
        Match m = BacktrackingMatcher.match(input, regex);

        assertTrue(
                "Regex " + regex + " should match '" + input + "'.",
                m.hasMatch && m.matched().equals(input));
    }

    private static void assertNotMatches(String input, RAst regex) {
        Match m = BacktrackingMatcher.match(input, regex);

        assertFalse(
                "Regex " + regex + " should NOT match '" + input + "'.",
                m.hasMatch);
    }
}
