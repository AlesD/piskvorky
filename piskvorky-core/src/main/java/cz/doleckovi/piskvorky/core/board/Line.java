package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.board.Stone;
import cz.doleckovi.piskvorky.core.evaluator.Pattern;

import java.util.BitSet;

public class Line {

	static boolean terminal(BitSet playerStones, BitSet opponentStones, int index) {
		return Integer.min(playerStones.nextClearBit(index), opponentStones.nextSetBit(index)) - Integer.max(playerStones.previousClearBit(index), opponentStones.previousSetBit(index)) > Pattern.LENGTH;
	}

	private final BitSet whiteStones;
	private final BitSet blackStones;

	private final boolean terminal;

	private Line(BitSet whiteStones, BitSet blackStones, boolean terminal) {
		this.whiteStones = whiteStones;
		this.blackStones = blackStones;
		this.terminal = terminal;
	}

	public Line(int length) {
		whiteStones = blackStones = new BitSet(length + 1);
		whiteStones.set(length);
		terminal = false;
	}

	public int getLength() {
		return whiteStones.length();
	}

	public boolean isTerminal() {
		return terminal;
	}

	public Line withStone(int index, Stone stone) {
		assert !whiteStones.get(index);
		assert !blackStones.get(index);
		return switch (stone) {
			case WHITE -> {
				var newWhiteStones = (BitSet) whiteStones.clone();
				newWhiteStones.set(index);
				yield new Line(newWhiteStones, blackStones, terminal || terminal(newWhiteStones, blackStones, index));
			}
			case BLACK -> {
				var newBlackStones = (BitSet) blackStones.clone();
				newBlackStones.set(index);
				yield new Line(whiteStones, newBlackStones, terminal || terminal(newBlackStones, whiteStones, index));
			}
			case BLOCK -> {
				var newWhiteStones = (BitSet) whiteStones.clone();
				newWhiteStones.set(index);
				var newBlackStones = (BitSet) blackStones.clone();
				newBlackStones.set(index);
				yield new Line(newWhiteStones, newBlackStones, terminal);
			}
			case EMPTY -> this;
		};
	}

}
