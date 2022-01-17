package pl.marcinchwedczuk.reng.parser;

import org.junit.jupiter.api.Test;

public class CharListTest {
    @Test
    public void it_works() {
        CharList list = new CharList();

        assertEquals(new char[0], list.toArray());

        list.add('a');
        assertEquals(new char[] { 'a' }, list.toArray());

        list.add('b');
        assertEquals(new char[] { 'a', 'b' }, list.toArray());

        list.addAll(new char[] { 'x', 'y', 'z' });
        assertEquals(new char[] { 'a', 'b', 'x', 'y', 'z' }, list.toArray());
    }

    private void assertEquals(char[] left, char[] right) {
        org.junit.jupiter.api.Assertions.assertEquals(new String(left), new String(right));
    }
}
