package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.action.Context;
import java.util.Collection;
import java.util.StringJoiner;

@CommandInfo(name = ListCommandsCommand.NAME, description = "Report all known commands")
public class ListCommandsCommand implements Command {

	public static final String NAME = "list_commands";

	private final Collection<String> commandNames;

    public ListCommandsCommand(Collection<String> commandNames) {
        this.commandNames = commandNames;
    }

    @Override
    public String execute(Context context) {
        var joiner = new StringJoiner("\n");
        commandNames.forEach(joiner::add);
        return joiner.toString();
    }

	@Override
	public String toString() {
		return NAME;
	}
}
