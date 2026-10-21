package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.action.Context;
import java.util.StringJoiner;

@CommandInfo(name = KnownCommandCommand.NAME, description = "Tell whether a command is known")
public class KnownCommandCommand implements Command {

	public static final String NAME = "known_command";

	private final String commandName;
    private final boolean known;

    public KnownCommandCommand(String commandName, boolean known) {
        this.commandName = commandName;
        this.known = known;
    }

    @Override
    public String execute(Context context) {
        return Boolean.toString(known);
    }

    @Override
    public String toString() {
        return NAME + ' ' + commandName;
    }

}
