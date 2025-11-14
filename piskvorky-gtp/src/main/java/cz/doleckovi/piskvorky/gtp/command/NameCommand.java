package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.CommandContext;
import cz.doleckovi.piskvorky.gtp.GTPCommand;

@Command(name = NameCommand.NAME, description = "Name of the engine")
public class NameCommand implements GTPCommand {

    public static final String NAME = "name";
	public static final NameCommand INSTANCE = new NameCommand("piskvorky");

    private final String name;

    public NameCommand(String name) {
        this.name = name;
    }

    @Override
    public String execute(CommandContext context) {
        return name;
    }
}
