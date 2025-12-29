package cz.doleckovi.piskvorky.api.board;

import java.util.List;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

/** Line descriptor. */
public final class LineDescriptor {

    private final int index;
    private final Direction direction;
    private final List<FieldAddress> fields;

    public LineDescriptor(int index, Direction direction, List<FieldAddress> fields) throws NullPointerException {
        this.index = index;
        this.direction = requireNonNull(direction, "Parameter 'direction' is null");
        this.fields = List.copyOf(requireNonNull(fields, "Parameter 'fields' is null"));
    }

    /** Gets line index.
     * @return Index of line described by this instance
     */
    public int index() {
        return index;
    }

    /** Gets line direction.
     * @return Line direction
     */
    public Direction direction() {
        return direction;
    }

    /** Gets line length.
     * @return Line length
     */
    public int length() {
        return fields.size();
    }

    /** Gets field address.
     * @param offset Offset from the line start
     * @return Field address
     * @throws IndexOutOfBoundsException if offset is negative or greater than or equal to length
     */
    public FieldAddress field(int offset) throws IndexOutOfBoundsException {
        return fields.get(offset);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof final LineDescriptor that) {
            return direction == that.direction && fields.equals(that.fields);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return hash(direction, fields);
    }

    @Override
    public String toString() {
        return fields.getFirst() + ".." +  fields.getLast();
    }

}
