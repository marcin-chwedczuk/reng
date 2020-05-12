package pl.marcinchwedczuk.reng.parser;

public class RParseException extends RuntimeException {
    public RParseException(int pos, String message) {
        super("pos " + pos + ": " + message);
    }
}
