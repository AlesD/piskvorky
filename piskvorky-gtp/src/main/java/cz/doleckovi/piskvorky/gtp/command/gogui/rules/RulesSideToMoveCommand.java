package cz.doleckovi.piskvorky.gtp.command.gogui.rules;

import cz.doleckovi.piskvorky.gtp.action.Context;
import cz.doleckovi.piskvorky.gtp.command.Command;
import cz.doleckovi.piskvorky.gtp.command.CommandException;
import cz.doleckovi.piskvorky.gtp.command.CommandInfo;

@CommandInfo(name = RulesSideToMoveCommand.NAME, description = "Report the side to move")
public class RulesSideToMoveCommand implements Command {

	public static final String NAME = "gogui-rules_side_to_move";

	@Override
	public String execute(Context context) throws CommandException {
		return switch (context.getGame().sideToMove()) {
			case WHITE -> "white";
			case BLACK -> "black";
		};
	}

	@Override
	public String toString() {
		return NAME;
	}

}
