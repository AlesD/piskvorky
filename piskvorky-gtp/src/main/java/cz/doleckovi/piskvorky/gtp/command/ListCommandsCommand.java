package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.CommandContext;
import cz.doleckovi.piskvorky.gtp.GTPCommand;

import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

@Command(name = NameCommand.NAME, description = "List of commands, one per row")
public class ListCommandsCommand implements GTPCommand {

    public static final String NAME = "list_commands";
	public static final ListCommandsCommand INSTANCE = new ListCommandsCommand();

    private final Collection<String> commandNames;

    public ListCommandsCommand(Collection<String> commandNames) {
        this.commandNames = commandNames;
    }

	private ListCommandsCommand() {
		this(List.copyOf(CommandFactoryImpl.ALL_COMMANDS.keySet()));
	}

    @Override
    public String execute(CommandContext context) {
        var joiner = new StringJoiner("\n");
        commandNames.forEach(joiner::add);
        return joiner.toString();
    }

}
