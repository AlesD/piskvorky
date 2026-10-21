package cz.doleckovi.piskvorky.gtp.action;

import cz.doleckovi.piskvorky.gtp.command.Command;
import cz.doleckovi.piskvorky.gtp.command.CommandAction;
import cz.doleckovi.piskvorky.gtp.command.CommandException;
import cz.doleckovi.piskvorky.gtp.trait.Cancelable;
import cz.doleckovi.piskvorky.gtp.trait.Canceling;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;


/**
 * Verifies the result contract of the actions: {@link Result} carries the structured
 * outcome and {@link Result#format()} produces the framed block — starting with
 * {@code =}/{@code ?}, echoing the id when present, carrying the answer text verbatim
 * and ending with the terminating {@code '\n'} (the blank response terminator is
 * contributed by {@code LineWriter.writeln}).
 */
class ActionFramingTest implements WithAssertions {

	/** Command body carrying an execution trait, like the real canceling commands do. */
	private static class CancelingCommand implements Command, Canceling {
		@Override
		public String execute(Context context) {
			return "ok";
		}
	}

	/** Command body cancelable by {@link #cancel()} — like a real long-running search. */
	private static class CancelableCommand implements Command, Cancelable {
		volatile boolean canceled = false;

		@Override
		public boolean cancelDuringExecution() {
			canceled = true; // cooperative: sets provider-owned state the body watches
			return Cancelable.super.cancelDuringExecution();
		}

		@Override
		public String execute(Context context) {
			return "ok";
		}
	}

	private static Command returning(String result) {
		return context -> result;
	}

	private static Command throwing(RuntimeException failure) {
		return context -> {
			throw failure;
		};
	}

	/** Wraps a command into the plain action variant. */
	private static CommandAction commandAction(String id, Command command) {
		return new CommandAction(id, command);
	}

	@Test
	void successfulCommandActionWithId() {
		Result result = commandAction("1", returning("result")).execute(TestContext.SILENT);
		assertThat(result).isInstanceOfSatisfying(Result.Success.class, success -> {
			assertThat(success.id()).isEqualTo("1");
			assertThat(success.answer()).isEqualTo("result");
			assertThat(success.format()).isEqualTo("= 1 result\n");
		});
	}

	@Test
	void successfulCommandActionWithoutId() {
		assertThat(commandAction(null, returning("result")).execute(TestContext.SILENT).format()).isEqualTo("= result\n");
	}

	@Test
	void commandActionWithNullResult() {
		assertThat(commandAction("1", returning(null)).execute(TestContext.SILENT).format())
				.as("Null answer produces marker-only response").isEqualTo("= 1\n");
	}

	@Test
	void commandActionWithEmptyResult() {
		assertThat(commandAction("1", returning("")).execute(TestContext.SILENT).format())
				.as("Empty answer produces marker-only response").isEqualTo("= 1\n");
	}

	@Test
	void commandActionWithMultiLineResult() {
		assertThat(commandAction(null, returning("line1\nline2\n")).execute(TestContext.SILENT).format())
				.as("Only first line carries the marker, text kept verbatim")
				.isEqualTo("= line1\nline2\n\n");
	}

	@Test
	void commandActionWithResultWithoutTrailingNewline() {
		assertThat(commandAction(null, returning("line1\nline2")).execute(TestContext.SILENT).format())
				.isEqualTo("= line1\nline2\n");
	}

	@Test
	void failedCommandActionWithId() {
		var action = commandAction("7", throwing(new CommandException("bad argument")));
		assertThat(action.execute(TestContext.SILENT))
				.isInstanceOfSatisfying(Result.Failure.class, failure -> {
					assertThat(failure.format()).isEqualTo("? 7 bad argument\n");
				});
	}

	@Test
	void failedCommandActionWithoutId() {
		var action = commandAction(null, throwing(new CommandException("bad argument")));
		assertThat(action.execute(TestContext.SILENT))
				.isInstanceOfSatisfying(Result.Failure.class, failure -> {
					assertThat(failure.format()).isEqualTo("? bad argument\n");
				});
	}

	@Test
	void failedCommandActionWithoutMessage() {
		assertThat(commandAction("7", throwing(new CommandException(""))).execute(TestContext.SILENT).format())
				.as("Empty message produces marker-only response, no trailing space")
				.isEqualTo("? 7\n");
	}

	@Test
	void unexpectedExceptionIsFramedAsError() {
		var action = commandAction("1", context -> {
			throw new IllegalStateException("bug");
		});
		assertThat(action.execute(TestContext.SILENT).format()).isEqualTo("? 1 unknown error\n");
	}

	@Test
	void messagelessUnexpectedExceptionIsUnknownErrorToo() {
		var action = commandAction(null, context -> {
			throw new NullPointerException();
		});
		assertThat(action.execute(TestContext.SILENT).format()).isEqualTo("? unknown error\n");
	}

	@Test
	void noOpActionProducesNoOutput() {
		assertThat(NoOpAction.INSTANCE.execute(TestContext.SILENT)).isSameAs(Result.IGNORED);
		assertThat(Result.IGNORED.format()).isNull();
	}

	@Test
	void interruptActionProducesNoOutput() {
		assertThat(new InterruptAction().execute(TestContext.SILENT)).isSameAs(Result.IGNORED);
	}

	@Test
	void syntaxErrorActionWithId() {
		Result result = new SyntaxErrorAction("3", "syntax error at line 1:7").execute(TestContext.SILENT);
		assertThat(result).isInstanceOf(Result.Failure.class);
		assertThat(result.format()).isEqualTo("? 3 syntax error at line 1:7\n");
	}

	@Test
	void syntaxErrorActionWithoutId() {
		assertThat(new SyntaxErrorAction(null, "syntax error at line 1:7").execute(TestContext.SILENT).format())
				.isEqualTo("? syntax error at line 1:7\n");
	}

	@Test
	void factoryWrapsCommandAndCopiesTraits() {
		var command = new CancelingCommand();
		var action = CommandAction.of("1", command);
		assertThat(action)
				.as("Command traits are visible on the action").isInstanceOf(Canceling.class);
		var result = action.execute(TestContext.SILENT);
		assertThat(result)
				.as("Command traits are copied onto the result").isInstanceOf(Canceling.class);
		assertThat(result.format()).isEqualTo("= 1 ok\n");
	}

	@Test
	void factoryKeepsCancelableBodyCancelableOnAction() {
		// Cancelable is behavior, not a copied marker: the action must
		// implement it itself, forwarding to the body...
		var command = new CancelableCommand();
		var action = CommandAction.of("1", command);
		assertThat(action).isInstanceOf(Cancelable.class);
		((Cancelable) action).cancelDuringExecution();
		assertThat(command.canceled)
				.as("cancel() reached the body through the action").isTrue();
		// ...and cancelability must not leak further down the pipeline
		assertThat(action.execute(TestContext.SILENT))
				.as("Result of a finished action has nothing to cancel").isNotInstanceOf(Cancelable.class);
	}

	@Test
	void factoryCreatesOtherActions() throws Exception {
		var factory = new ActionFactory() {};
		assertThat(factory.createNoOpAction()).isSameAs(NoOpAction.INSTANCE);
		assertThat(factory.createInterruptAction()).isInstanceOf(InterruptAction.class);
		assertThat(factory.createInterruptAction()).isInstanceOf(Canceling.class);
		assertThat(factory.createSyntaxErrorAction("1", "message").execute(TestContext.SILENT).format()).isEqualTo("? 1 message\n");
	}

}
