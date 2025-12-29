package cz.doleckovi.piskvorky.gtp.command;

import java.util.StringJoiner;

@CommandInfo(name = "piskvorky-config", description = "Configure piskvorky")
public class PiskvorkyConfigCommand implements Command {

	private final String key;
	private final String value;

	public PiskvorkyConfigCommand(String key, String value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public String call() throws CommandException {
		throw new UnsupportedOperationException("Command is not implemented yet");
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", PiskvorkyConfigCommand.class.getSimpleName() + "[", "]")
				.add("key='" + key + "'")
				.add("value='" + value + "'")
				.toString();
	}

}
