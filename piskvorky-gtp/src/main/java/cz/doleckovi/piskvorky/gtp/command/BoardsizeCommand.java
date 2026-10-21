package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.action.Context;
import cz.doleckovi.piskvorky.gtp.trait.Canceling;

@CommandInfo(name = BoardsizeCommand.NAME, description = "Sets board size")
public class BoardsizeCommand implements Command, Canceling {

	public static final String NAME = "boardsize";

	private final int size;

	public BoardsizeCommand(int size) {
		this.size = size;
	}

	@Override
	public String execute(Context context) throws CommandException {
		try {
			context.setBoard(size);
		} catch (Exception e) {
			throw new CommandException("unacceptable size", e);
		}
		return null;
	}

	@Override
	public String toString() {
		return NAME + ' ' + size;
	}
}
