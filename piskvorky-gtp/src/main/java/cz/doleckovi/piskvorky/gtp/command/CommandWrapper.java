package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.Command;
import cz.doleckovi.piskvorky.gtp.CommandContext;

public class CommandWrapper implements Command {

	private Integer id;
	private GTPCommand command;

	public CommandWrapper(Integer id, GTPCommand command) {
		this.id = id;
		this.command = command;
	}

	@Override
	public String execute(CommandContext context) {
        var response = command.execute(context);
        var result = new StringBuilder();
        if (id != null) result.append(id);
        if (!result.isEmpty()) result.append(' ').append(response);
        return result.toString();
	}

}
