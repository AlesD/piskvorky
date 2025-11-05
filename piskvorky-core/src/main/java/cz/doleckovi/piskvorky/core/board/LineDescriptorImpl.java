package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.board.Direction;
import cz.doleckovi.piskvorky.api.board.LineDescriptor;

import java.util.List;
import java.util.Objects;

public class LineDescriptorImpl implements LineDescriptor {

	private final Direction direction;
	private final List<FieldAddressImpl> fields;

	LineDescriptorImpl(Direction direction, List<FieldAddressImpl> fields) {
        if (fields.isEmpty())
            throw new IllegalArgumentException("Empty fields");
		this.direction = Objects.requireNonNull(direction);
		this.fields = List.copyOf(fields);
	}

    @Override
	public Direction direction() {
		return direction;
	}

	@Override
	public int length() {
		return fields.size();
	}

	@Override
	public FieldAddressImpl field(int offset) {
		return fields.get(offset);
	}

    @Override
    public boolean equals(Object o) {
        if (o instanceof final LineDescriptorImpl that) {
            return direction == that.direction && fields.size() == that.fields.size() && fields.getFirst() == that.fields.getFirst();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(direction, fields.size(), fields.getFirst());
    }

    @Override
    public String toString() {
        return fields.getFirst() + "  " +  fields.getLast();
    }

}
