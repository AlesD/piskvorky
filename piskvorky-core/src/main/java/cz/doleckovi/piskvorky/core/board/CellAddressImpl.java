package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.board.CellAddress;

import java.util.Objects;

/** Base cell address implementation. */
public class CellAddressImpl implements CellAddress {

    private final int line;
    private final int offset;

    CellAddressImpl(int line, int offset) {
        this.line = line;
        this.offset = offset;
    }

    /** Gets line index.
     * @return Line index
     */
    public int line() {
        return line;
    }

    @Override
    public int offset() {
        return offset;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof CellAddressImpl that) {
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
