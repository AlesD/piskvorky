package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.api.game.Side;
import cz.doleckovi.piskvorky.gtp.action.Context;

@CommandInfo(name = PlayCommand.NAME, description = "Plays given move")
public class PlayCommand implements Command {

	public static final String NAME = "play";

	private final int column;
	private final int row;
	private final Side side;

	public PlayCommand(Side side, int column, int row) {
		this.side = side;
		this.column = column;
		this.row = row;
	}

	@Override
	public String execute(Context context) throws CommandException {
		try {
			context.getGame().move(side, column, row);
		} catch (Exception e) {
			throw new CommandException("illegal move", e);
		}
		return null;
	}

	@Override
	public String toString() {
		return NAME + ' ' + side.stone + '(' + column + ',' + row + ')';
	}
}
