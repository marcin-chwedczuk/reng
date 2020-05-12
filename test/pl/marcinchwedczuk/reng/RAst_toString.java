package pl.marcinchwedczuk.reng;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RAst_toString {
    @Test public void toString_works() {
        RAst r = RAst.concat(
                RAst.group('B'),
                RAst.star(
                        RAst.alternative(RAst.group('0'), RAst.group('1'))),
                RAst.group('E'));

        assertEquals(
                "B(0|1)*E",
                r.toString());
    }
}
