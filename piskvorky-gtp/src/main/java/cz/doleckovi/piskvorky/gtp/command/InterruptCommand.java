package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.command.trait.Interrupting;

public class InterruptCommand implements Interrupting, Command {

	public static final InterruptCommand INSTANCE = new InterruptCommand();

	private InterruptCommand() {
		super();
	}

	@Override
	public String call() {
		// Do Nothing
		return null;
	}

	@Override
	public String toString() {
		return InterruptCommand.class.getSimpleName() + "[]";
	}

}
