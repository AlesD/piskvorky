package cz.doleckovi.piskvorky.api.game.notification;

import cz.doleckovi.piskvorky.api.game.Game;
import cz.doleckovi.piskvorky.api.game.Move;

import java.util.StringJoiner;

public class MoveEvent extends GameEvent {

	private final Move move;

	public MoveEvent(Object source, Game game, Move move) {
		super(source, game);
		this.move = move;
	}

	public Move getMove() {
		return move;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", MoveEvent.class.getSimpleName() + "[", "]")
				.add("game=" + game.getId())
				.add("move=" + move)
				.toString();
	}

}
