package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.command.trait.Interrupting;

import java.util.StringJoiner;

@CommandInfo(name = "boardsize", description = "Sets board size")
public class BoardSizeCommand implements Command, SessionAction, Interrupting {

	private final int size;

	public BoardSizeCommand(int size) {
		this.size = size;
	}

	@Override
	public String call() throws CommandException {
		try {
			session().setBoard(size);
		} catch (Exception e) {
			throw new CommandException(this, "unacceptable size", e);
		}
		return null;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", BoardSizeCommand.class.getSimpleName() + "[", "]")
				.add("size=" + size)
				.toString();
	}
}
