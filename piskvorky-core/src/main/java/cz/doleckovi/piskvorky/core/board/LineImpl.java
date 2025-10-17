package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.Constants;
import cz.doleckovi.piskvorky.api.board.Stone;

import java.util.Arrays;

class LineImpl implements Line {

	static boolean terminal(Stone[] stones, int offset) {
		var stone = stones[offset];
		int count = 1;
		int index = offset;
		while (++index < stones.length && stones[index] == stone)
			++count;
		while (--offset >= 0 && stones[offset] == stone)
			++count;
		return count >= Constants.SIZE;
	}

	private final Stone[] stones;
	private final boolean terminal;

	private LineImpl(Stone[] stones, boolean terminal) {
		this.stones = stones;
		this.terminal = terminal;
	}

	public LineImpl(int length) {
		assert length >= Constants.SIZE;
		stones = new Stone[length];
		Arrays.fill(stones, Stone.EMPTY);
		terminal = false;
	}

	@Override
	public int length() {
		return stones.length;
	}

	@Override
	public boolean isTerminal() {
		return terminal;
	}

	@Override
	public Stone stone(int offset) {
		return stones[offset];
	}

	@Override
	public LineImpl withStone(int offset, Stone stone) {
		if (terminal)
			throw new IllegalStateException("Can't place stone in on line in terminal state");
		if (stones[offset] != Stone.EMPTY)
			throw new IllegalArgumentException("Can't place stone on non-empty field");
		if (stone == Stone.EMPTY)
			return this;
		var newStones = stones.clone();
		newStones[offset] = stone;
		if (stone == Stone.BLOCK)
			return new LineImpl(newStones, false);
		return new LineImpl(newStones, terminal(newStones, offset));
	}

}
