package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.CommandContext;
import cz.doleckovi.piskvorky.gtp.GTPCommand;

@Command(name = KnownCommandCommand.NAME, description = "Tests support for specific command")
public class KnownCommandCommand implements GTPCommand {

    public static final String NAME = "known_command";

    private final boolean known;

    public KnownCommandCommand(String commandName) {
        this.known = CommandFactoryImpl.ALL_COMMANDS.containsKey(commandName);
    }

    @Override
    public String execute(CommandContext context) {
        return Boolean.toString(known);
    }

}
