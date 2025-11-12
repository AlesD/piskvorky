package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.CommandContext;
import cz.doleckovi.piskvorky.gtp.GTPCommand;

import java.util.List;
import java.util.StringJoiner;

public class ListCommandsCommand implements GTPCommand {

    public static final String NAME = "list_commands";
    public static final String DESCRIPTION = "List of commands, one per row";

    private final List<String> commandNames;

    public ListCommandsCommand(List<String> commandNames) {
        this.commandNames = commandNames;
    }

    @Override
    public String execute(CommandContext context) {
        var joiner = new StringJoiner("\n");
        commandNames.forEach(joiner::add);
        return joiner.toString();
    }

}
