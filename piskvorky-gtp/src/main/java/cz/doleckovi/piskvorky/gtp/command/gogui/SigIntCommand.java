package cz.doleckovi.piskvorky.gtp.command.gogui;

import cz.doleckovi.piskvorky.gtp.action.Context;
import cz.doleckovi.piskvorky.gtp.command.Command;

import cz.doleckovi.piskvorky.gtp.execution.IntSignalHandler;

/** Provides PID so caller can send INT to interrupt commands.
 * <p>Signal processing done by {@link IntSignalHandler}</p>
 */
public class SigIntCommand implements Command {

	public static final String NAME = "gogui-sigint";

	@Override
	public String execute(Context context) {
		return Long.toString(ProcessHandle.current().pid());
	}

	@Override
	public String toString() {
		return NAME;
	}

}
