package pl.marcinchwedczuk.reng;

import java.util.Objects;

public class InputPositionMarker {
    final int pos;

    public InputPositionMarker(int pos) {
        this.pos = pos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputPositionMarker that = (InputPositionMarker) o;
        return pos == that.pos;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos);
    }
}
