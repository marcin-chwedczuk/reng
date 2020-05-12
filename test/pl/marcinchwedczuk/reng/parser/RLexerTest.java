package pl.marcinchwedczuk.reng.parser;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class RLexerTest {
    @Test public void splits_input_to_tokens() {
        List<RToken> tokens = new RLexer("^\\^\\n(a)").split();

        List<RToken> expected = Arrays.asList(
                new RToken(RTokenType.AT_BEGINNING, '^', 0),
                new RToken(RTokenType.CHARACTER, '^', 1),
                new RToken(RTokenType.CHARACTER, '\n', 3),
                new RToken(RTokenType.LPAREN, '(', 5),
                new RToken(RTokenType.CHARACTER, 'a', 6),
                new RToken(RTokenType.RPAREN, ')', 7),
                new RToken(RTokenType.EOF, '\0', 8)
        );

        Assert.assertEquals(expected, tokens);
    }
}
