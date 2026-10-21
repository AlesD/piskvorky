package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.action.Context;
import java.util.StringJoiner;

@CommandInfo(name = "echo", description = "Print ")
public class EchoCommand implements Command {

	private final String text;

    public EchoCommand(String text) {
        this.text = text;
    }

    @Override
    public String execute(Context context) {
        return text;
    }

	@Override
	public String toString() {
		return new StringJoiner(", ", EchoCommand.class.getSimpleName() + "[", "]")
				.add("text='" + text + "'")
				.toString();
	}
}
