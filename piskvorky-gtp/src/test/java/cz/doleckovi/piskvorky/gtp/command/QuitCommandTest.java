package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.action.Action;
import cz.doleckovi.piskvorky.gtp.action.Context;
import cz.doleckovi.piskvorky.gtp.action.Result;
import cz.doleckovi.piskvorky.gtp.action.TestContext;
import cz.doleckovi.piskvorky.gtp.execution.ActionProcessingLoop;
import cz.doleckovi.piskvorky.gtp.execution.DiscardingResultSink;
import cz.doleckovi.piskvorky.gtp.execution.SubmitRejectedException;
import cz.doleckovi.piskvorky.gtp.trait.Cancelable;
import cz.doleckovi.piskvorky.gtp.trait.Canceling;
import cz.doleckovi.piskvorky.gtp.trait.Shutdown;
import cz.doleckovi.piskvorky.gtp.trait.Sync;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies that {@link QuitCommand} carries the {@code Canceling + Shutdown} markers
 * (TODO items {@code [8]}, {@code [21]}) and that the pipeline honors them end-to-end:
 * submitting {@code quit} terminalizes the stage without blocking the submitter, a
 * running cancelable command is wound down by the {@code Canceling} sweep at the moment
 * of acceptance, and the produced {@link Result} cascades the end downstream.
 * Session-level consequences (transport closed, end-of-life cleanup) are pinned by
 * {@code SessionTest}.
 */
class QuitCommandTest {

	private static Action quitAction(String id) {
		return CommandAction.of(id, new QuitCommand());
	}

	private static ThreadFactory daemonFactory() {
		return runnable -> {
			var thread = new Thread(runnable);
			thread.setDaemon(true);
			return thread;
		};
	}

	@Test
	void quitCarriesCancelingAndShutdownButNotSync() {
		var quit = new QuitCommand();
		assertThat(quit).isInstanceOf(Canceling.class);
		assertThat(quit).isInstanceOf(Shutdown.class);
		assertThat(quit).as("terminal stays an asynchronous submission")
				.isNotInstanceOf(Sync.class);
	}

	@Test
	void factoryCopiesQuitTraitsIntoActionAndResult() {
		var action = quitAction("1");
		assertThat(action).isInstanceOf(Shutdown.class);
		assertThat(action).isNotInstanceOf(Sync.class);
		assertThat(action.execute(TestContext.SILENT))
				.as("Shutdown marker reaches the loop through the result")
				.isInstanceOf(Shutdown.class)
				.as("quit stays a value-resulted action")
				.isInstanceOfSatisfying(Result.class, result -> {
					assertThat(result.format()).isEqualTo("= 1\n");
				});
	}

	@Test
	void quitSubmissionTerminalizesTheStageWithoutBlocking() throws Exception {
		var loop = new ActionProcessingLoop(daemonFactory(), TestContext.SILENT, new DiscardingResultSink(), stage -> { });
		var future = loop.submit(quitAction("9"));
		assertThat(future.get(2, TimeUnit.SECONDS).format()).isEqualTo("= 9\n");
		assertThatThrownBy(() -> loop.submit(quitAction("10")))
				.as("the stage terminalized by the carrier refuses later work")
				.isInstanceOf(SubmitRejectedException.class);
	}

	@Test
	void quitCancelsRunningCancelableBeforeItFinishes() throws Exception {
		CountDownLatch blockerStarted = new CountDownLatch(1);
		CountDownLatch blockerRelease = new CountDownLatch(1);
		var blocker = (Action) context -> {
			blockerStarted.countDown();
			try {
				blockerRelease.await(2, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			return Result.IGNORED;
		};
		var waiter = new CancelableWaiter();
		var loop = new ActionProcessingLoop(daemonFactory(), TestContext.SILENT, new DiscardingResultSink(), stage -> { });
		loop.submit(blocker);
		assertThat(blockerStarted.await(2, TimeUnit.SECONDS)).isTrue();
		var waiterAction = CommandAction.of(null, waiter);
		var waiterFuture = loop.submit(waiterAction);
		// let the blocker finish so the cancelable actually starts running
		blockerRelease.countDown();
		assertThat(waiter.started.await(2, TimeUnit.SECONDS)).isTrue();
		// the Canceling sweep already ran at acceptance (quit is not Sync - the
		// submission itself would return before the chain completed); await the drain
		var quitFuture = loop.submit(quitAction("3"));
		assertThat(waiter.canceled)
				.as("running cancelable command was canceled the moment quit was accepted").isTrue();
		assertThat(quitFuture.get(2, TimeUnit.SECONDS).format()).isEqualTo("= 3\n");
		assertThat(waiterFuture.get(2, TimeUnit.SECONDS).format()).isEqualTo("= waited\n");
	}

	/** Cancelable command blocking in {@code execute} until {@code cancel()} releases it. */
	private static class CancelableWaiter implements Command, Cancelable {

		final CountDownLatch started = new CountDownLatch(1);
		private final CountDownLatch release = new CountDownLatch(1);
		volatile boolean canceled = false;

		@Override
		public boolean cancelDuringExecution() {
			canceled = true;
			release.countDown(); // cooperative wind-down: release the parked body
			return false;
		}

		@Override
		public String execute(Context context) throws CommandException {
			started.countDown();
			try {
				release.await(2, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			return "waited";
		}
	}

}
