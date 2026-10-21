package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.action.Context;
import java.util.Arrays;
import java.util.StringJoiner;

public class UnknownCommand implements Command {

	public static final String NAME = "unknown_command";

	private final String commandName;
	private final Object[] args;

	public UnknownCommand(String commandName, Object... args) {
		this.commandName = commandName;
		this.args = args;
	}

	@Override
	public String execute(Context context) throws CommandException {
		return "unknown command";
	}

	@Override
	public String toString() {
		var result = new StringJoiner(" ").add(commandName);
		if (args != null) for (var arg : args) result.add(arg.toString());
		return result.toString();
	}

}
