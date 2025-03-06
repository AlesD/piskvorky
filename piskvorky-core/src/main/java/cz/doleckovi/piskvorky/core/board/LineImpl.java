package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.board.Line;
import cz.doleckovi.piskvorky.api.board.Stone;
import cz.doleckovi.piskvorky.core.evaluator.Pattern;

import java.util.BitSet;

public class LineImpl implements Line {

	static boolean terminal(BitSet playerStones, BitSet opponentStones, int index) {
		return Integer.min(playerStones.nextClearBit(index), opponentStones.nextSetBit(index)) - Integer.max(playerStones.previousClearBit(index), opponentStones.previousSetBit(index)) > Pattern.LENGTH;
	}

	private final BitSet whiteStones;
	private final BitSet blackStones;

	private final boolean terminal;

	private LineImpl(BitSet whiteStones, BitSet blackStones, boolean terminal) {
		this.whiteStones = whiteStones;
		this.blackStones = blackStones;
		this.terminal = terminal;
	}

	public LineImpl(int length) {
		whiteStones = blackStones = new BitSet(length + 1);
		whiteStones.set(length);
		terminal = false;
	}

	@Override
	public int getLength() {
		return whiteStones.length();
	}

	@Override
	public boolean isTerminal() {
		return terminal;
	}

	@Override
	public Stone stone(int offset) {
		if (whiteStones.get(offset)) {
			if (blackStones.get(offset))
				return Stone.BLOCK;
			return Stone.WHITE;
		} else if (blackStones.get(offset))
			return Stone.BLACK;
		return Stone.EMPTY;
	}

	@Override
	public LineImpl withStone(int offset, Stone stone) {
		if (terminal)
			throw new IllegalStateException("Can't place stone in terminal state");
		if (stone(offset) != Stone.EMPTY)
			throw new IllegalArgumentException("Can't place stone on non-empty field");
		return switch (stone) {
			case WHITE -> {
				var newWhiteStones = (BitSet) whiteStones.clone();
				newWhiteStones.set(offset);
				yield new LineImpl(newWhiteStones, blackStones, terminal(newWhiteStones, blackStones, offset));
			}
			case BLACK -> {
				var newBlackStones = (BitSet) blackStones.clone();
				newBlackStones.set(offset);
				yield new LineImpl(whiteStones, newBlackStones, terminal(newBlackStones, whiteStones, offset));
			}
			case BLOCK -> {
				var newWhiteStones = (BitSet) whiteStones.clone();
				newWhiteStones.set(offset);
				var newBlackStones = (BitSet) blackStones.clone();
				newBlackStones.set(offset);
				yield new LineImpl(newWhiteStones, newBlackStones, false);
			}
			case EMPTY -> this;
		};
	}

}
