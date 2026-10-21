package cz.doleckovi.piskvorky.gtp.command.gogui.rules;

import cz.doleckovi.piskvorky.gtp.action.Context;
import cz.doleckovi.piskvorky.gtp.command.Command;
import cz.doleckovi.piskvorky.gtp.command.CommandException;

public class RulesBoardGfxCommand implements Command {

	public static final String NAME = "gogui-rules_board_gfx";

	@Override
	public String execute(Context context) throws CommandException {
		return null;
	}

	@Override
	public String toString() {
		return NAME;
	}

}
