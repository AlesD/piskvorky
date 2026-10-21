package cz.doleckovi.piskvorky.gtp.command.gogui.rules;

import cz.doleckovi.piskvorky.gtp.action.Context;
import cz.doleckovi.piskvorky.gtp.command.Command;
import cz.doleckovi.piskvorky.gtp.command.CommandException;

public class RulesCapturedCountCommand implements Command {

	public static final String NAME = "gogui-rules_captured_count";

	@Override
	public String execute(Context context) throws CommandException {
		return "0 0";
	}

	@Override
	public String toString() {
		return NAME;
	}

}
