package cz.doleckovi.piskvorky.gtp.command;

@CommandInfo(name = "name", description = "Report the name of the program")
public class NameCommand implements Command {

	public static final NameCommand INSTANCE = new NameCommand("piskvorky");

    private final String name;

    public NameCommand(String name) {
        this.name = name;
    }

    @Override
    public String execute() {
        return name;
    }
}
