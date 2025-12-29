package cz.doleckovi.piskvorky.gtp.command;

import java.util.StringJoiner;

@CommandInfo(name = "version", description = "Report the version number of the program")
public class VersionCommand implements Command {

	private final String version;

	public VersionCommand(String version) {
        this.version = version;
    }

    @Override
    public String call() {
        return version;
    }

	@Override
	public String toString() {
		return new StringJoiner(", ", VersionCommand.class.getSimpleName() + "[", "]")
				.add("version='" + version + "'")
				.toString();
	}

}
