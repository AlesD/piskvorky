package cz.doleckovi.piskvorky.gtp.command;

import java.util.Collection;
import java.util.StringJoiner;

@CommandInfo(name = "list_commands", description = "Report all known commands")
public class ListCommandsCommand implements Command {

	private final Collection<String> commandNames;

    public ListCommandsCommand(Collection<String> commandNames) {
        this.commandNames = commandNames;
    }

    @Override
    public String call() {
        var joiner = new StringJoiner("\n");
        commandNames.forEach(joiner::add);
        return joiner.toString();
    }

	@Override
	public String toString() {
		return new StringJoiner(", ", ListCommandsCommand.class.getSimpleName() + "[", "]")
				.add("commandNames=" + commandNames)
				.toString();
	}
}
