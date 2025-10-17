package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.board.Side;
import cz.doleckovi.piskvorky.api.search.Move;

import java.util.Objects;

public record MoveImpl(Side side, int column, int row) implements Move {

	public MoveImpl(Side side, int column, int row) {
		this.side = Objects.requireNonNull(side);
		this.column = column;
		this.row = row;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		MoveImpl move = (MoveImpl) o;
		return column == move.column && row == move.row && side == move.side;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(side.stone)
				.append('[')
				.append(column)
				.append(',')
				.append(row)
				.append(']')
				.toString();
	}

}
