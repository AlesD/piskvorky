package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.action.Context;

public class UndoCommand implements Command {

	public static final String NAME = "undo";

	@Override
	public String execute(Context context) throws CommandException {
		throw new CommandException("cannot undo");
	}

	@Override
	public String toString() {
		return NAME;
	}

}
