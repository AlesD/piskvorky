package cz.doleckovi.piskvorky.gtp.command.gogui.rules;

import cz.doleckovi.piskvorky.gtp.action.Context;
import cz.doleckovi.piskvorky.gtp.command.Command;
import cz.doleckovi.piskvorky.gtp.command.CommandException;
import cz.doleckovi.piskvorky.gtp.command.CommandInfo;

@CommandInfo(name = RulesBoardSizeCommand.NAME, description = "Report the board size in GoGui rules format")
public class RulesBoardSizeCommand implements Command {

	public static final String NAME = "gogui-rules_board_size";

	@Override
	public String execute(Context context) throws CommandException {
		return Integer.toString(context.getBoard().size());
	}

	@Override
	public String toString() {
		return NAME;
	}
}
