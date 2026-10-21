package cz.doleckovi.piskvorky.gtp.command.gogui;

import cz.doleckovi.piskvorky.gtp.action.Context;
import cz.doleckovi.piskvorky.gtp.command.Command;
import cz.doleckovi.piskvorky.gtp.trait.Canceling;

public class InterruptCommand implements Canceling, Command {

	public static final String NAME = "gogui-interrupt";

	public static final InterruptCommand INSTANCE = new InterruptCommand(null);

	private final String result;

	private InterruptCommand(String result) {
		this.result = result;
	}

	public InterruptCommand() {
		this("");
	}

	@Override
	public String execute(Context context) {
		// Do Nothing
		return null;
	}

	@Override
	public String toString() {
		return NAME;
	}

}
