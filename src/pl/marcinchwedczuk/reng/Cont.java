package pl.marcinchwedczuk.reng;

/**
 * Represents continuation.
 */
@FunctionalInterface
public interface Cont {
    boolean run();
}
