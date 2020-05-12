package pl.marcinchwedczuk.reng.parser;

import org.junit.Assert;
import org.junit.Test;
import pl.marcinchwedczuk.reng.RAst;

import static org.junit.Assert.*;

public class RParserTest {
    @Test public void parse_smoke() {
        RAst ast = RParser.parse("^(foo|bar)$");

        Assert.assertEquals(ast.toString(), "^(foo|bar)$");
    }
}
