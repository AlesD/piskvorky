package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.api.game.Side;
import cz.doleckovi.piskvorky.gtp.action.Context;
import cz.doleckovi.piskvorky.gtp.trait.Cancelable;

import java.util.StringJoiner;

@CommandInfo(name = "reg_genmove", description = "Generates move for given side")
public class RegGenMoveCommand implements Command, Cancelable {

	private final Side side;

	public RegGenMoveCommand(Side side) {
		this.side = side;
	}

	@Override
	public String execute(Context context) throws CommandException {
		throw new UnsupportedOperationException("Command is not implemented yet");
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", RegGenMoveCommand.class.getSimpleName() + "[", "]")
				.add("side=" + side)
				.toString();
	}
}
