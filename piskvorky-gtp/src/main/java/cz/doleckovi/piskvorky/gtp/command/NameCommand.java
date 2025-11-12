package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.CommandContext;
import cz.doleckovi.piskvorky.gtp.GTPCommand;

public class NameCommand implements GTPCommand {

    public static final String NAME = "name";
    public static final String DESCRIPTION = "Name of the engine";

    private final String name;

    public NameCommand(String name) {
        this.name = name;
    }

    @Override
    public String execute(CommandContext context) {
        return name;
    }
}
