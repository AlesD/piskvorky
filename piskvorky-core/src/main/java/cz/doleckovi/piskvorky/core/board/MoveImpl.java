package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.search.Player;
import cz.doleckovi.piskvorky.api.search.Move;

import java.util.Objects;

public record MoveImpl(Player player, int column, int row) implements Move {

	public MoveImpl(Player player, int column, int row) {
		this.player = Objects.requireNonNull(player);
		this.column = column;
		this.row = row;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		MoveImpl move = (MoveImpl) o;
		return column == move.column && row == move.row && player == move.player;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(player.stone)
				.append('[')
				.append(column)
				.append(',')
				.append(row)
				.append(']')
				.toString();
	}

}
