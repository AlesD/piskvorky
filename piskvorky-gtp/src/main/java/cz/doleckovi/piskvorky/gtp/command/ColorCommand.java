package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.Vertex;

import java.util.StringJoiner;

@CommandInfo(name = "color", description = "Return the color at a vertex")
public class ColorCommand implements Command, SessionAction {

	private final Vertex vertex;

	public ColorCommand(Vertex vertex) {
		this.vertex = vertex;
	}

	@Override
	public String call() throws CommandException {
		throw new UnsupportedOperationException("Command is not implemented yet");
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", ColorCommand.class.getSimpleName() + "[", "]")
				.add("vertex=" + vertex)
				.toString();
	}
}
