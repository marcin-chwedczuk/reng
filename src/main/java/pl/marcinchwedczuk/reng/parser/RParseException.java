package pl.marcinchwedczuk.reng.parser;

public class RParseException extends RuntimeException {
    public final int column;

    public RParseException(int pos, String message) {
        super(message);
        this.column = pos;
    }
}
