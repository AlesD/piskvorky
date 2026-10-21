package cz.doleckovi.piskvorky.gtp.command.gogui.rules;

import cz.doleckovi.piskvorky.gtp.action.Context;
import cz.doleckovi.piskvorky.gtp.command.Command;
import cz.doleckovi.piskvorky.gtp.command.CommandException;
import cz.doleckovi.piskvorky.gtp.command.CommandInfo;

@CommandInfo(name = RulesFinalResultCommand.NAME, description = "Report the final game result")
public class RulesFinalResultCommand implements Command {

	public static final String NAME = "gogui-rules_final_result";

	@Override
	public String execute(Context context) throws CommandException {
		var game = context.getGame();
		if (game.position().isTerminal()) {
			return switch (game.sideToMove()) {
				case WHITE -> "Black won";
				case BLACK -> "White won";
			};
		}
		return "Draw";
	}

	@Override
	public String toString() {
		return NAME;
	}

}
