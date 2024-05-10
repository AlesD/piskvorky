package cz.doleckovi.piskvorky.api.notification;

import cz.doleckovi.piskvorky.api.board.Move;

import java.util.StringJoiner;

public class MoveEvent extends GameEvent {

	private final Move move;

	public MoveEvent(Object source, int gameId, Move move) {
		super(source, gameId);
		this.move = move;
	}

	public Move getMove() {
		return move;
	}

	@Override
	protected StringJoiner toString(StringJoiner fields) {
		return super.toString(fields).add("move=" + move);
	}

}
