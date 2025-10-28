package cz.doleckovi.piskvorky.core.simple;

import cz.doleckovi.piskvorky.api.board.Direction;
import cz.doleckovi.piskvorky.api.board.FieldAddress;
import cz.doleckovi.piskvorky.api.board.LineDescriptor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

class SimpleLineDescriptor implements LineDescriptor {

    private final int index;
	private final Direction direction;
	private final List<SimpleFieldAddress> fields;

	SimpleLineDescriptor(int index, Direction direction, List<SimpleFieldAddress> fields) {
		this.index = index;
		this.direction = Objects.requireNonNull(direction);
		this.fields = List.copyOf(fields);
	}

    @Override
    public int index() {
        return index;
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
	public FieldAddress field(int offset) {
		return fields.get(offset);
	}

    @Override
    public boolean equals(Object o) {
        if (o instanceof final SimpleLineDescriptor that) {
            return index == that.index && direction == that.direction && fields.equals(that.fields);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, direction, fields);
    }

    @Override
    public String toString() {
        return fields.getFirst() + "  " +  fields.getLast();
    }

}
