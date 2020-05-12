package pl.marcinchwedczuk.reng.parser;

import org.junit.Assert;
import org.junit.Test;
import pl.marcinchwedczuk.reng.RAst;

import static org.junit.Assert.*;

public class RParserTest {
    @Test public void parse_single_letter() {
        RAst ast = RParser.parse("a");
        assertEquals("a", ast.toString());
    }

    @Test public void parse_escape_characters() {
        RAst ast = RParser.parse("\\n");
        assertEquals("\n", ast.toString());

        ast = RParser.parse("\\(\\[\\{");
        assertEquals("([{", ast.toString());
    }

    @Test public void parse_char_group() {
        RAst ast = RParser.parse("[abc]");
        assertEquals("[abc]", ast.toString());
    }

    @Test public void parse_char_group_with_range() {
        RAst ast = RParser.parse("[0-9]");
        assertEquals("[0123456789]", ast.toString());
    }

    @Test public void parse_negated_char_group() {
        RAst ast = RParser.parse("[^abc]");
        assertEquals("[^abc]", ast.toString());
    }

    @Test public void parse_anchors() {
        RAst ast = RParser.parse("^");
        assertEquals("^", ast.toString());

        ast = RParser.parse("$");
        assertEquals("$", ast.toString());
    }

    @Test public void parse_parentheses() {
        RAst ast = RParser.parse("(((a)))");
        assertEquals("a", ast.toString());
    }

    @Test public void parse_concat() {
        RAst ast = RParser.parse("abcd");
        assertEquals("abcd", ast.toString());

        ast = RParser.parse("a[1-2]b[3-4]");
        assertEquals("a[12]b[34]", ast.toString());
    }

    @Test public void parse_alternative() {
        RAst ast = RParser.parse("a|b|c|d");
        assertEquals("a|b|c|d", ast.toString());

        ast = RParser.parse("a|[1-2]|b|[3-4]");
        assertEquals("a|[12]|b|[34]", ast.toString());
    }

    @Test public void parse_star() {
        RAst ast = RParser.parse("a*");
        assertEquals("a*", ast.toString());
    }

    @Test public void parse_plus() {
        RAst ast = RParser.parse("a+");
        assertEquals("aa*", ast.toString());
    }

    // TODO: Ranges {1,3} and ?

    @Test public void parse_smoke() {
        RAst ast = RParser.parse("^(foo|bar)$");

        assertEquals(ast.toString(), "^(foo|bar)$");
    }
}
