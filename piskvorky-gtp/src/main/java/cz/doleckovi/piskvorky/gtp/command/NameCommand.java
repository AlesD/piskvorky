package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.action.Context;
import java.util.StringJoiner;

@CommandInfo(name = NameCommand.NAME, description = "Report the name of the program")
public class NameCommand implements Command {

	public static final String NAME = "name";

	private final String name;

    public NameCommand(String name) {
        this.name = name;
    }

    @Override
    public String execute(Context context) {
        return name;
    }

	@Override
	public String toString() {
		return NAME;
	}
}
