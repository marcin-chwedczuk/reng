package pl.marcinchwedczuk.reng;

public class Input {
    public static Input of(String s) { return new Input(s); }

    private final String input;
    // pos - index of first not yet seen character
    private int pos;

    public Input(String s) {
        input = s;
        pos = 0;
    }

    public int currentPos() {
        return pos;
    }

    public boolean atBeginning() {
        return pos == 0;
    }

    public boolean atEnd() {
        return pos == input.length();
    }

    public void advance(int nchars) {
        if ((pos + nchars) > input.length())
            throw new IllegalArgumentException("nchars: " + nchars);

        pos += nchars;
    }

    public char current() {
        return input.charAt(pos);
    }

    public InputMarker mark() {
        return new InputMarker(pos);
    }

    public void goTo(InputMarker m) {
        if (m.pos < 0 || m.pos > input.length())
            throw new IllegalArgumentException("marker pos: " + m.pos);

        pos = m.pos;
    }
}
