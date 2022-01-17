package pl.marcinchwedczuk.reng;

/**
 * Represents a continuation.
 * See: https://marcin-chwedczuk.github.io/continuations-in-java
 */
@FunctionalInterface
public interface Cont {
    boolean run();
}
