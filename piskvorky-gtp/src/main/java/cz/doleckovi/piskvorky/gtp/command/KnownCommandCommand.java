package cz.doleckovi.piskvorky.gtp.command;

import java.util.StringJoiner;

@CommandInfo(name = "known_command", description = "Tell whether a command is known")
public class KnownCommandCommand implements Command {

	private final String commandName;
    private final boolean known;

    public KnownCommandCommand(String commandName, boolean known) {
        this.commandName = commandName;
        this.known = known;
    }

    @Override
    public String call() {
        return Boolean.toString(known);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", KnownCommandCommand.class.getSimpleName() + "[", "]")
                .add("commandName='" + commandName + "'")
                .add("known=" + known)
                .toString();
    }
}
