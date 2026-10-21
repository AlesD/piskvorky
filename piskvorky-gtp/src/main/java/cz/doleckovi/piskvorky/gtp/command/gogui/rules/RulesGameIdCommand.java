package cz.doleckovi.piskvorky.gtp.command.gogui.rules;

import cz.doleckovi.piskvorky.gtp.action.Context;
import cz.doleckovi.piskvorky.gtp.command.Command;
import cz.doleckovi.piskvorky.gtp.command.CommandException;

public class RulesGameIdCommand implements Command {

	public static final String NAME = "gogui-rules_game_id";

	@Override
	public String execute(Context context) throws CommandException {
		return "Piskvorky";
	}

	@Override
	public String toString() {
		return NAME;
	}

}
