package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.api.game.Side;
import cz.doleckovi.piskvorky.api.position.Stone;
import cz.doleckovi.piskvorky.gtp.action.Move;
import cz.doleckovi.piskvorky.gtp.action.TestContext;
import cz.doleckovi.piskvorky.gtp.action.Vertex;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Verifies {@link CompositeCommandFactory}. */
class CompositeCommandFactoryTest {

	/** Factory recording every creation call and returning a per-method marker body. */
	private static class RecordingFactory implements CommandFactory {

		final List<String> calls = new ArrayList<>();
		private final Collection<String> supported;
		final Command simpleMarker = context -> "simple";
		final Command numberMarker = context -> "number";
		final Command sideMarker = context -> "side";
		final Command vertexMarker = context -> "vertex";
		final Command moveMarker = context -> "move";
		final Command kvpMarker = context -> "kvp";
		final Command textMarker = context -> "text";

		RecordingFactory(String... supportedCommands) {
			this.supported = List.of(supportedCommands);
		}

		@Override
		public Collection<String> supportedCommands() {
			return supported;
		}

		@Override
		public Command createSimpleCommand(String commandName) {
			calls.add("simple:" + commandName);
			return simpleMarker;
		}

		@Override
		public Command createNumberCommand(String commandName, int arg) {
			calls.add("number:" + commandName + ":" + arg);
			return numberMarker;
		}

		@Override
		public Command createSideCommand(String commandName, Side side) {
			calls.add("side:" + commandName + ":" + side);
			return sideMarker;
		}

		@Override
		public Command createVertexCommand(String commandName, Vertex vertex) {
			calls.add("vertex:" + commandName + ":" + vertex);
			return vertexMarker;
		}

		@Override
		public Command createMoveCommand(String commandName, List<Move> moves) {
			calls.add("move:" + commandName + ":" + moves);
			return moveMarker;
		}

		@Override
		public Command createKVPCommand(String commandName, String key, String value) {
			calls.add("kvp:" + commandName + ":" + key + ":" + value);
			return kvpMarker;
		}

		@Override
		public Command createTextCommand(String commandName, String text) {
			calls.add("text:" + commandName + ":" + text);
			return textMarker;
		}
	}

	@Test
	void supportedCommandsAggregateUnionOfMemberNames() {
		var first = new RecordingFactory("alpha", "beta");
		var second = new RecordingFactory("beta", "gamma");
		var composite = new CompositeCommandFactory(first, second);
		assertThat(composite.supportedCommands()).containsExactly(
				"alpha", "beta", "gamma", "known_command", "list_commands");
	}

	@Test
	void commandCreationDelegatesToDeclaringFactory() {
		var first = new RecordingFactory("alpha");
		var second = new RecordingFactory("gamma");
		var composite = new CompositeCommandFactory(List.of(first, second));
		var vertex = new Vertex(1, 2);
		assertThat(composite.createVertexCommand("gamma", vertex)).isSameAs(second.vertexMarker);
		assertThat(second.calls).containsExactly("vertex:gamma:" + vertex);
		assertThat(first.calls).isEmpty();
	}

	@Test
	void delegationMatchesNamesExactlyAndPassesArgumentsThrough() {
		var factory = new RecordingFactory("alpha");
		var composite = new CompositeCommandFactory(factory);
		var move = new Move(Stone.BLACK, new Vertex(3, 4));
		assertThat(composite.createSimpleCommand("alpha")).isSameAs(factory.simpleMarker);
		assertThat(composite.createNumberCommand("alpha", 7)).isSameAs(factory.numberMarker);
		assertThat(composite.createSideCommand("alpha", Side.WHITE)).isSameAs(factory.sideMarker);
		assertThat(composite.createMoveCommand("alpha", List.of(move))).isSameAs(factory.moveMarker);
		assertThat(composite.createKVPCommand("alpha", "k", "v")).isSameAs(factory.kvpMarker);
		assertThat(factory.calls).containsExactly(
				"simple:alpha",
				"number:alpha:7",
				"side:alpha:WHITE",
				"move:alpha:[" + move + "]",
				"kvp:alpha:k:v");
	}

	@Test
	void differentlyCasedNameIsNotTheDeclaredCommand() {
		var factory = new RecordingFactory("alpha");
		var composite = new CompositeCommandFactory(factory);
		assertThat(composite.createSimpleCommand("ALPHA")).isInstanceOf(UnknownCommand.class);
		assertThat(composite.createSimpleCommand("Alpha")).isInstanceOf(UnknownCommand.class);
		assertThat(factory.calls).isEmpty();
	}

	@Test
	void firstFactoryWinsForDuplicateNames() {
		var first = new RecordingFactory("alpha");
		var second = new RecordingFactory("alpha", "beta");
		var composite = new CompositeCommandFactory(first, second);
		assertThat(composite.createSimpleCommand("alpha")).isSameAs(first.simpleMarker);
		assertThat(second.calls).isEmpty();
	}

	@Test
	void nameThatNoMemberImplementsProducesUnknownCommand() {
		var composite = new CompositeCommandFactory(new RecordingFactory("alpha"));
		assertThat(composite.createSimpleCommand("nope")).isInstanceOf(UnknownCommand.class);
	}

	@Test
	void listCommandsIsAnsweredFromUnionAndNeverDelegated() throws Exception {
		var first = new RecordingFactory("alpha", ListCommandsCommand.NAME);
		var second = new RecordingFactory("beta", ListCommandsCommand.NAME);
		var composite = new CompositeCommandFactory(first, second);
		var command = composite.createSimpleCommand(ListCommandsCommand.NAME);
		assertThat(command).isInstanceOf(ListCommandsCommand.class);
		// the map is sorted - names are reported alphabetically, the composite's
		// own entries among them
		assertThat(command.execute(TestContext.SILENT))
				.isEqualTo("alpha\nbeta\nknown_command\nlist_commands");
		// command names are case sensitive (spec §3.2) - a differently cased
		// spelling is just some name no member declares
		assertThat(composite.createSimpleCommand("List_Commands")).isInstanceOf(UnknownCommand.class);
		assertThat(first.calls).isEmpty();
		assertThat(second.calls).isEmpty();
	}

	@Test
	void knownCommandIsAnsweredFromUnionAndNeverDelegated() throws Exception {
		var first = new RecordingFactory("alpha", KnownCommandCommand.NAME);
		var second = new RecordingFactory("beta");
		var composite = new CompositeCommandFactory(first, second);
		assertThat(composite.createTextCommand(KnownCommandCommand.NAME, "alpha"))
				.isInstanceOfSatisfying(KnownCommandCommand.class,
						command -> assertThat(command.execute(TestContext.SILENT)).isEqualTo("true"));
		assertThat(composite.createTextCommand(KnownCommandCommand.NAME, "beta"))
				.isInstanceOfSatisfying(KnownCommandCommand.class,
						command -> assertThat(command.execute(TestContext.SILENT)).isEqualTo("true"));
		assertThat(composite.createTextCommand(KnownCommandCommand.NAME, "BETA"))
				.isInstanceOfSatisfying(KnownCommandCommand.class,
						command -> assertThat(command.execute(TestContext.SILENT)).isEqualTo("false"));
		assertThat(composite.createTextCommand(KnownCommandCommand.NAME, "zeta"))
				.isInstanceOfSatisfying(KnownCommandCommand.class,
						command -> assertThat(command.execute(TestContext.SILENT)).isEqualTo("false"));
		assertThat(first.calls).isEmpty();
		assertThat(second.calls).isEmpty();
	}

	@Test
	void reservedNamesAreNotDelegatedThroughForeignArgumentShapes() {
		var composite = new CompositeCommandFactory(
				new RecordingFactory(ListCommandsCommand.NAME, KnownCommandCommand.NAME));
		assertThat(composite.createSimpleCommand(KnownCommandCommand.NAME)).isInstanceOf(UnknownCommand.class);
		assertThat(composite.createTextCommand(ListCommandsCommand.NAME, "x")).isInstanceOf(UnknownCommand.class);
		assertThat(composite.createNumberCommand(ListCommandsCommand.NAME, 1)).isInstanceOf(UnknownCommand.class);
	}

	@Test
	void requiredCommandsAreAlwaysSupportedEvenWithoutAMemberDeclaringThem() throws Exception {
		var composite = new CompositeCommandFactory(new RecordingFactory("alpha"));
		assertThat(composite.supportedCommands()).contains(ListCommandsCommand.NAME, KnownCommandCommand.NAME);
		assertThat(composite.createSimpleCommand(ListCommandsCommand.NAME)).isInstanceOf(ListCommandsCommand.class);
		assertThat(composite.createTextCommand(KnownCommandCommand.NAME, "alpha"))
				.isInstanceOfSatisfying(KnownCommandCommand.class,
						command -> assertThat(command.execute(TestContext.SILENT)).isEqualTo("true"));
		// the composite's own commands are known to known_command as well
		assertThat(composite.createTextCommand(KnownCommandCommand.NAME, ListCommandsCommand.NAME))
				.isInstanceOfSatisfying(KnownCommandCommand.class,
						command -> assertThat(command.execute(TestContext.SILENT)).isEqualTo("true"));
	}
}
