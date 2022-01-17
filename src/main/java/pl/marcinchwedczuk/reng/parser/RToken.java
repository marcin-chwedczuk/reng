package pl.marcinchwedczuk.reng.parser;

import java.util.Objects;

public class RToken {
    public final RTokenType type;
    public final char c;
    public final int pos;

    public RToken(RTokenType type, char c, int pos) {
        this.type = type;
        this.c = c;
        this.pos = pos;
    }

    @Override
    public String toString() {
        return "(" + type + " at " + pos + "; '" + c + "')";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RToken rToken = (RToken) o;
        return c == rToken.c &&
                pos == rToken.pos &&
                type == rToken.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, c, pos);
    }
}
