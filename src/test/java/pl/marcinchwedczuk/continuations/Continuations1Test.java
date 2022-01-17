package pl.marcinchwedczuk.continuations;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Continuations1Test {
    @FunctionalInterface
    private interface Cont<R> {
        Thunk apply(R result);
    }

    @FunctionalInterface
    private interface Thunk {
        Thunk run();
    }

    private static void trampoline(Thunk thunk) {
        while (thunk != null) {
            thunk = thunk.run();
        }
    }

    private static <T> Cont<T> endCall(Consumer<T> call) {
        return r -> {
            call.accept(r);
            return null;
        };
    }

    private static Thunk add(int a, int b, Cont<Integer> cont) {
        int sum = a + b;
        return () -> cont.apply(sum);
    }

    private static Thunk add(int a, int b, int c, Cont<Integer> cont) {
        return add(a, b, sum ->
                add(sum, c, cont));
    }

    private static Thunk multiply(int a, int b, Cont<Integer> cont) {
        int product = a * b;
        return () -> cont.apply(product);
    }

    private static Thunk eq(int a, int b, Cont<Boolean> cont) {
        boolean result = (a == b);
        return () -> cont.apply(result);
    }

    private static Thunk lt(int a, int b, Cont<Boolean> cont) {
        boolean result  = (a < b);
        return () -> cont.apply(result);
    }

    private static Thunk iff(boolean expr,
                             Cont<Boolean> trueBranch,
                             Cont<Boolean> falseBranch) {
        return (expr)
                ? () -> trueBranch.apply(true)
                : () -> falseBranch.apply(false);
    }

    private static Thunk factorial(int n, Cont<Integer> cont) {
        return eq(n, 0, isNZero ->
                iff(isNZero,
                        trueArg -> cont.apply(1),
                        falseArg -> add(n, -1, nm1 ->
                                factorial(nm1, fnm1 ->
                                        multiply(n, fnm1, cont)))));
    }

    @Test
    public void ramping_works() {
        AtomicInteger res = new AtomicInteger(-1);
        trampoline(factorial(4, endCall(res::set)));

        assertEquals(24, res.get());

        // No stack overflow
        trampoline(factorial(400000, endCall(res::set)));
    }
}
