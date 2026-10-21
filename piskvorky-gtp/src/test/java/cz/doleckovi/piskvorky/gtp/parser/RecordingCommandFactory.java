package cz.doleckovi.piskvorky.gtp.parser;

import cz.doleckovi.piskvorky.api.game.Side;
import cz.doleckovi.piskvorky.gtp.action.Move;
import cz.doleckovi.piskvorky.gtp.action.Vertex;
import cz.doleckovi.piskvorky.gtp.command.Command;
import cz.doleckovi.piskvorky.gtp.command.CommandFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * {@link CommandFactory} test double recording the exact arguments the parser
 * extracted and returning a dummy command producing {@code "result"}.
 */
class RecordingCommandFactory implements CommandFactory {

	record Call(String kind, String name, Object arg) {}

	final List<Call> calls = new ArrayList<>();

	@Override
	public Collection<String> supportedCommands() {
		return List.of("simple", "number", "vertex", "side", "moves", "call", "kvp", "text");
	}

	@Override
	public Command createSimpleCommand(String name) {
		return record("simple", name, null);
	}

	@Override
	public Command createNumberCommand(String name, int arg) {
		return record("number", name, arg);
	}

	@Override
	public Command createVertexCommand(String name, Vertex vertex) {
		return record("vertex", name, vertex);
	}

	@Override
	public Command createSideCommand(String name, Side side) {
		return record("side", name, side);
	}

	@Override
	public Command createMoveCommand(String name, List<Move> moves) {
		return record("moves", name, moves);
	}

	@Override
	public Command createKVPCommand(String name, String key, String value) {
		return record("kvp", name, key + "=" + value);
	}

	@Override
	public Command createTextCommand(String name, String text) {
		return record("text", name, text);
	}

	private Command record(String kind, String name, Object arg) {
		calls.add(new Call(kind, name, arg));
		return context -> "result";
	}

}
