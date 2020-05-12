package pl.marcinchwedczuk.reng;

public class Match {
    public final boolean hasMatch;
    public final String input;
    public final int start;
    public final int end;

    public Match(String input, boolean hasMatch, int start, int end) {
        this.input = input;
        this.hasMatch = hasMatch;
        this.start = start;
        this.end = end;
    }

    public String matched()  {
        if (!hasMatch) return null;
        return input.substring(start, end);
    }
}
