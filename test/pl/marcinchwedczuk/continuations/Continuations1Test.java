package pl.marcinchwedczuk.continuations;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

public class Continuations1Test {
    private interface Cont0<R> {
        Ramp apply(R result);
    }

    private interface Ramp {
        Ramp run();
    }

    private static void exec(Ramp ramp) {
        while (ramp != null) {
            ramp = ramp.run();
        }
    }

    private static <T> Cont0<T> endCall(Consumer<T> call) {
        return r -> {
            call.accept(r);
            return null;
        };
    }

    private static Ramp add(int a, int b, Cont0<Integer> cont) {
        int ab = a + b;
        return () -> cont.apply(ab);
    }

    private static Ramp add(int a, int b, int c, Cont0<Integer> cont) {
        return add(a, b, ab ->
                add(ab, c, cont));
    }

    private static Ramp multiply(int a, int b, Cont0<Integer> cont) {
        int ab = a * b;
        return () -> cont.apply(ab);
    }

    private static Ramp eq(int a, int b, Cont0<Boolean> cont) {
        boolean aEqB = (a == b);
        return () -> cont.apply(aEqB);
    }

    private static Ramp lt(int a, int b, Cont0<Boolean> cont) {
        return () -> cont.apply(a < b);
    }

    private static Ramp iff(boolean expr,
                            Cont0<Boolean> trueBranch,
                            Cont0<Boolean> falseBranch) {
        return (expr)
                ? () -> trueBranch.apply(true)
                : () -> falseBranch.apply(false);
    }

    private static Ramp factorial(int n, Cont0<Integer> cont) {
        return eq(n, 0, isNZero ->
                iff(isNZero,
                        trueArg -> cont.apply(1),
                        falseArg -> add(n, -1, nm1 ->
                                factorial(nm1, fnm1 ->
                                        multiply(n, fnm1, cont)))));
    }

    @Test public void ramping_works() {
        AtomicInteger res = new AtomicInteger(-1);
        exec(factorial(4, endCall(res::set)));

        Assert.assertEquals(24, res.get());

        // No stack overflow
        exec(factorial(400000, endCall(res::set)));
    }
}
