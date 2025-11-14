package cz.doleckovi.piskvorky.gtp.session;

import cz.doleckovi.piskvorky.gtp.Command;
import cz.doleckovi.piskvorky.gtp.CommandContext;
import cz.doleckovi.piskvorky.gtp.GTPCommand;

public class CommandWrapper implements Command {

	private Integer id;
	private GTPCommand command;

	public CommandWrapper(Integer id, GTPCommand command) {
		this.id = id;
		this.command = command;
	}

	@Override
	public String execute(CommandContext context) {
		try {
			var response = command.execute(context);
			context.
		}
	}
}
