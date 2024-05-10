package cz.doleckovi.piskvorky.api.game.notification;

import cz.doleckovi.piskvorky.api.game.Game;

import java.util.EventObject;
import java.util.StringJoiner;

public class GameEvent extends EventObject {

	protected final Game game;

	public GameEvent(Object source, Game game) {
		super(source);
		this.game = game;
	}

	public Game getGame() {
		return game;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", GameEvent.class.getSimpleName() + "[", "]")
				.add("game=" + game.getId())
				.toString();
	}
}
