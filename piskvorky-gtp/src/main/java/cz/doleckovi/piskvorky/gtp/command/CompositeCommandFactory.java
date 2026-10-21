package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.api.game.Side;
import cz.doleckovi.piskvorky.gtp.action.Move;
import cz.doleckovi.piskvorky.gtp.action.Vertex;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Command factory composing other factories.
 * <p>{@value ListCommandsCommand#NAME} and {@value KnownCommandCommand#NAME}
 * are required commands (spec §6.1) that the composite always answers itself.</p>
 */
public class CompositeCommandFactory implements CommandFactory {

	private final Map<String, CommandFactory> factoriesByCommand;

	public CompositeCommandFactory(CommandFactory... factories) {
		this(List.of(factories));
	}

	public CompositeCommandFactory(Collection<? extends CommandFactory> factories) {
		factoriesByCommand = new TreeMap<>();
		for (var factory : factories) {
			for (var commandName : factory.supportedCommands()) {
				factoriesByCommand.putIfAbsent(commandName, factory);
			}
		}
		factoriesByCommand.put(ListCommandsCommand.NAME, null);
		factoriesByCommand.put(KnownCommandCommand.NAME, null);
	}

	@Override
	public Collection<String> supportedCommands() {
		return factoriesByCommand.keySet();
	}

	@Override
	public Command createSimpleCommand(String commandName) {
		if (ListCommandsCommand.NAME.equals(commandName)) {
			return new ListCommandsCommand(supportedCommands());
		}
		var factory = factoriesByCommand.get(commandName);
		return factory != null
				? factory.createSimpleCommand(commandName)
				: new UnknownCommand(commandName);
	}

	@Override
	public Command createNumberCommand(String commandName, int arg) {
		var factory = factoriesByCommand.get(commandName);
		return factory != null
				? factory.createNumberCommand(commandName, arg)
				: new UnknownCommand(commandName, arg);
	}

	@Override
	public Command createSideCommand(String commandName, Side side) {
		var factory = factoriesByCommand.get(commandName);
		return factory != null
				? factory.createSideCommand(commandName, side)
				: new UnknownCommand(commandName, side);
	}

	@Override
	public Command createVertexCommand(String commandName, Vertex vertex) {
		var factory = factoriesByCommand.get(commandName);
		return factory != null
				? factory.createVertexCommand(commandName, vertex)
				: new UnknownCommand(commandName, vertex);
	}

	@Override
	public Command createMoveCommand(String commandName, List<Move> moves) {
		var factory = factoriesByCommand.get(commandName);
		return factory != null
				? factory.createMoveCommand(commandName, moves)
				: new UnknownCommand(commandName, moves.toArray());
	}

	@Override
	public Command createKVPCommand(String commandName, String key, String value) {
		var factory = factoriesByCommand.get(commandName);
		return factory != null
				? factory.createKVPCommand(commandName, key, value)
				: new UnknownCommand(commandName, key, value);
	}

	@Override
	public Command createTextCommand(String commandName, String text) {
		if (KnownCommandCommand.NAME.equals(commandName)) {
			return new KnownCommandCommand(text, factoriesByCommand.containsKey(text));
		}
		var factory = factoriesByCommand.get(commandName);
		return factory != null
				? factory.createTextCommand(commandName, text)
				: new UnknownCommand(commandName, text);
	}
}
