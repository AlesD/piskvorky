package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.CommandContext;
import cz.doleckovi.piskvorky.gtp.GTPCommand;

import java.util.List;
import java.util.StringJoiner;

public class KnownCommandCommand implements GTPCommand {

    public static final String NAME = "known_command";
    public static final String DESCRIPTION = "Tests support for specific command";

    private final boolean known;

    public KnownCommandCommand(boolean known) {
        this.known = known;
    }

    @Override
    public String execute(CommandContext context) {
        return Boolean.toString(known);
    }

}
