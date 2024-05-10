package cz.doleckovi.piskvorky.api.notification;

import java.util.EventObject;
import java.util.StringJoiner;

public abstract class GameEvent extends EventObject {

	protected final int gameId;

	public GameEvent(Object source, int gameId) {
		super(source);
		this.gameId = gameId;
	}

	public int getGameId() {
		return gameId;
	}

	protected StringJoiner toString(StringJoiner fields) {
		fields.add("gameId=" + gameId);
		return fields;
	}

	@Override
	public String toString() {
		return toString(new StringJoiner(", ", GameEvent.class.getSimpleName() + "[", "]")).toString();
	}

}
