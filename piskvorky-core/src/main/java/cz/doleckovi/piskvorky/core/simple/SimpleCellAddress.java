package cz.doleckovi.piskvorky.core.simple;

import java.util.Objects;

import cz.doleckovi.piskvorky.api.board.CellAddress;

class SimpleCellAddress implements CellAddress {

    private final int line;
    private final int offset;

    SimpleCellAddress(int line, int offset) {
        this.line = line;
        this.offset = offset;
    }

    @Override
    public int line() {
        return line;
    }

    @Override
    public int offset() {
        return offset;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof SimpleCellAddress that) {
            return line == that.line && offset == that.offset;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, offset);
    }

    @Override
    public String toString() {
        return line + "+" + offset;
    }

}
