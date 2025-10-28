package cz.doleckovi.piskvorky.core.simple;

import cz.doleckovi.piskvorky.api.board.Direction;
import cz.doleckovi.piskvorky.api.board.FieldAddress;
import cz.doleckovi.piskvorky.api.board.LineDescriptor;

import java.util.List;

class SimpleLineDescriptor implements LineDescriptor {

	private final int size;
	private final Direction direction;
	private final List<SimpleFieldAddress> fields;

	SimpleLineDescriptor(int size, Direction direction, List<SimpleFieldAddress> fields) {
		this.size = size;
		this.direction = direction;
		this.fields = fields;
	}

	@Override
	public int size() {
		return size;
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

}
