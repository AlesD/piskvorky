package cz.doleckovi.piskvorky.gtp.command;

@CommandInfo(name = "name", description = "Report the name of the program")
public class NameCommand implements Command {

	private final String name;

    public NameCommand(String name) {
        this.name = name;
    }

    @Override
    public String call() {
        return name;
    }
}
