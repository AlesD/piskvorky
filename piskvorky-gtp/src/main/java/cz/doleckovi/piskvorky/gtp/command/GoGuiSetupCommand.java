package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.Move;

import java.util.List;
import java.util.StringJoiner;

@CommandInfo(name = "gogui-setup", description = "Place stones on the board")
public class GoGuiSetupCommand implements Command, SessionAction {

	private final List<Move> moves;

	public GoGuiSetupCommand(List<Move> moves) {
		this.moves = List.copyOf(moves);
	}

	@Override
	public String call() throws CommandException {
		throw new UnsupportedOperationException("Command is not implemented yet");
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", GoGuiSetupCommand.class.getSimpleName() + "[", "]")
				.add("moves=" + moves)
				.toString();
	}
}
