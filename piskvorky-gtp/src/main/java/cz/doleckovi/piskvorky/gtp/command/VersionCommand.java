package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.action.Context;
import java.util.StringJoiner;

@CommandInfo(name = VersionCommand.NAME, description = "Report the version number of the program")
public class VersionCommand implements Command {

	public static final String NAME = "version";

	private final String version;

	public VersionCommand(String version) {
        this.version = version;
    }

    @Override
    public String execute(Context context) {
        return version;
    }

	@Override
	public String toString() {
		return new StringJoiner(", ", VersionCommand.class.getSimpleName() + "[", "]")
				.add("version='" + version + "'")
				.toString();
	}

}
