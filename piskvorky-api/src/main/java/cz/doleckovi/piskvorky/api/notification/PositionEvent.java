package cz.doleckovi.piskvorky.api.notification;

import cz.doleckovi.piskvorky.api.board.Position;

import java.util.StringJoiner;

public class PositionEvent extends GameEvent {

	private final Position position;

	public PositionEvent(Object source, int gameId, Position position) {
		super(source, gameId);
		this.position = position;
	}

	@Override
	protected StringJoiner toString(StringJoiner fields) {
		return super.toString(fields).add("position=" + position);
	}

}
