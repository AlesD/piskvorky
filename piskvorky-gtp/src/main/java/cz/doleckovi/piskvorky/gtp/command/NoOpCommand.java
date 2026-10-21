package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.action.Context;

public class NoOpCommand implements Command {

	public static final NoOpCommand INSTANCE = new NoOpCommand();

	private NoOpCommand() {
		// Do nothing
	}

	@Override
	public String execute(Context context) {
		// Do nothing
		return null;
	}

	@Override
	public String toString() {
		return NoOpCommand.class.getSimpleName();
	}

}
