package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.Move;

import java.util.List;
import java.util.StringJoiner;

@CommandInfo(name = "play", description = "Plays given move")
public class PlayCommand implements Command {

	private final Move move;

	public PlayCommand(List<Move> moves) {
		if (moves.size() != 1)
			throw new IllegalArgumentException("Expected 1 move, but got %d".formatted(moves.size()));
		this.move = moves.getFirst();
	}

	@Override
	public String call() throws CommandException {
		throw new UnsupportedOperationException("Command %s is not implemented yet");
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", PlayCommand.class.getSimpleName() + "[", "]")
				.add("move=" + move)
				.toString();
	}
}
