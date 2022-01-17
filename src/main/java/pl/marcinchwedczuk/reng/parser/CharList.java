package pl.marcinchwedczuk.reng.parser;

public class CharList {
    private char[] data = new char[0];
    private int nextFree = 0;

    public int size() {
        return nextFree;
    }

    public void add(char c) {
        ensureCapacity(size() + 1);
        data[nextFree++] = c;
    }

    public void addAll(char[] chars) {
        ensureCapacity(size() + chars.length);
        System.arraycopy(chars, 0, data, nextFree, chars.length);
        nextFree += chars.length;
    }

    public char[] toArray() {
        char[] tmp = new char[size()];
        System.arraycopy(data, 0, tmp, 0, size());
        return tmp;
    }

    private void ensureCapacity(int requiredCapacity) {
        if (requiredCapacity <= data.length)
            return;

        int newSize = Math.max(
                2*data.length,
                requiredCapacity);

        char[] newData = new char[newSize];
        System.arraycopy(data, 0,  newData, 0, size());

        this.data = newData;
    }
}
