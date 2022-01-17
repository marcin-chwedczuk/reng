package pl.marcinchwedczuk.continuations;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContinuationsTest {

    @FunctionalInterface
    private interface Cont<R> {
        void apply(R result);
    }

    private static void add(int a, int b, Cont<Integer> cont) {
        cont.apply(a + b);
    }

    private static void addLong(long a, long b, Cont<Long> cont) {
        cont.apply(a + b);
    }

    private static void multiply(int a, int b, Cont<Integer> cont) {
        cont.apply(a*b);
    }

    private static void eq(int a, int b, Cont<Boolean> cont) {
        cont.apply(a == b);
    }

    private static void lt(int a, int b, Cont<Boolean> cont) {
        cont.apply(a < b);
    }

    private static void gt(int a, int b, Cont<Boolean> cont) { cont.apply(a > b); }

    private static void iff(boolean expr,
                            Cont<Boolean> trueBranch,
                            Cont<Boolean> falseBranch) {
        if (expr) trueBranch.apply(true);
        else falseBranch.apply(false);
    }

    private static void factorial(int n, Cont<Integer> cont) {
        eq(n, 0, isNZero ->
            iff(isNZero,
                    trueArg -> cont.apply(1),
                    falseArg -> add(n, -1, nm1 ->
                            factorial(nm1, fnm1 ->
                                    multiply(n, fnm1, cont)))));
    }

    private static int factorial1(int n) {
        if (n == 0) return 1;
        return factorial1(n-1)*n;
    }

    @Test
    public void factorial_cont() {
        for (int n = 0; n < 7; n++) {
            AtomicLong contResult = new AtomicLong(-1);
            factorial(n, contResult::set);

            assertEquals(factorial1(n), contResult.longValue());
        }
    }

    private static int fib1(int n) {
        if (n < 2) return 1;
        return fib1(n-1) + fib1(n-2);
    }

    private static void fib(int n, Cont<Integer> cont) {
        lt(n, 2, nlt2 ->
                iff(nlt2,
                        falseArg -> cont.apply(1),
                        trueArg -> add(n, -1, nm1 ->
                                fib(nm1, fnm1 ->
                                        add(n, -2, nm2 ->
                                                fib(nm2, fnm2 ->
                                                        add(fnm1, fnm2, cont)))))));
    }

    private static long sum_imperative(int from, int to) {
        long sum = 0;
        for (int i = from; i <= to; i++) {
            sum += i; // naive implementation
        }
        return sum;
    }

    private static long sum_rec(int from, int to) {
        return (from > to)
                ? 0
                : from + sum_rec(from+1, to);
    }

    private static void sumCC(int from, int to, Cont<Long> cont) {
        gt(from, to, fromGreaterThanTo ->
                iff(fromGreaterThanTo,
                        x -> cont.apply(0L),
                        x -> add(from, 1, from1 ->
                                sumCC(from1, to, sumCC1 ->
                                        addLong(from, sumCC1, cont)))));
    }

    @Test public void fib_works() {
        for (int n = 0; n < 7; n++) {
            AtomicInteger contResult = new AtomicInteger(-1);
            fib(n, contResult::set);

            assertEquals(fib1(n), contResult.get());
        }
    }
}
