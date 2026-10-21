package cz.doleckovi.piskvorky.gtp.execution;

import cz.doleckovi.piskvorky.gtp.action.Action;
import cz.doleckovi.piskvorky.gtp.action.Result;
import cz.doleckovi.piskvorky.gtp.action.TestContext;
import cz.doleckovi.piskvorky.gtp.trait.Cancelable;
import cz.doleckovi.piskvorky.gtp.trait.Canceling;
import cz.doleckovi.piskvorky.gtp.trait.Shutdown;
import cz.doleckovi.piskvorky.gtp.trait.Traits;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

/**
 * Covers the queue-and-worker machinery of the {@link ActionProcessingLoop} stage: the
 * {@code Shutdown} accepted-and-last boundary at {@code submit} (terminalization never
 * blocks the submitter), the terminal drain, the settling of futures on every failure
 * path (body failure, {@code Error}, downstream refusal, rejection), the termination hook
 * fired on every exit path, and the closing of acceptance when the worker thread dies by
 * any means (TODO.md [7] machinery, general contract). The stage's trait semantics -
 * {@code Sync} blocking and {@code Canceling} interpretation - are pinned in
 * {@link ActionProcessingLoopTest}.
 */
class ActionProcessingLoopLifecycleTest {

	/** Stateless - one shared sink serves every stage built without a wire. */
	private static final DiscardingResultSink DISCARD = new DiscardingResultSink();

	private final List<String> processed = new CopyOnWriteArrayList<>();

	private static ThreadFactory daemonFactory() {
		return runnable -> {
			var thread = new Thread(runnable);
			thread.setDaemon(true);
			return thread;
		};
	}

	/** Action answering with {@link Result#IGNORED} and recording its name on execution. */
	private Action named(String name) {
		return context -> {
			processed.add(name);
			return Result.IGNORED;
		};
	}

	private static Action terminal(Action action) {
		return Traits.addTraits(action, Shutdown.class);
	}

	private ActionProcessingLoop loop() {
		return new ActionProcessingLoop(daemonFactory(), TestContext.SILENT, DISCARD, stage -> { });
	}

	/**
	 * Asserts the stage is gone: probes it until submission is refused. The probe either
	 * throws {@link SubmitRejectedException} (acceptance already closed) or is
	 * accepted just before the exit and cancelled by the closing block's drain - both
	 * outcomes prove the worker left {@code run()} and the final block ran.
	 */
	private static void assertStopped(ActionProcessingLoop loop) {
		for (int attempt = 0; attempt < 200; ++attempt) {
			try {
				var probe = loop.submit(context -> Result.IGNORED);
				try {
					probe.get(100, TimeUnit.MILLISECONDS);
				} catch (CancellationException drained) {
					return; // cancelled by the closing block
				} catch (TimeoutException stuck) {
					// queued behind a busy worker; the stage is still alive, keep probing
				} catch (Exception settled) {
					return; // answered or failed - the stage ran on, still not stopped
				}
			} catch (SubmitRejectedException refused) {
				return; // acceptance closed
			}
			try {
				Thread.sleep(20);
			} catch (InterruptedException interrupted) {
				Thread.currentThread().interrupt();
				fail("interrupted while probing");
			}
		}
		fail("execution loop neither stopped accepting nor cancelled its probe");
	}

	// ---- submission and trait basics --------------------------------------------------

	@Test
	void plainTaskSettlesFutureWithResult() throws Exception {
		var loop = loop();
		assertThat(loop.submit(named("work")).get(2, TimeUnit.SECONDS)).isSameAs(Result.IGNORED);
		assertThat(processed).containsExactly("work");
	}

	@Test
	void terminationHookFiresWhenWorkerExits() throws Exception {
		var terminated = new CountDownLatch(1);
		var hook = new AtomicReference<ExecutionLoop<Action, Result>>();
		var loop = new ActionProcessingLoop(daemonFactory(), TestContext.SILENT, DISCARD, stage -> {
			hook.set(stage);
			terminated.countDown();
		});
		assertThat(terminated.getCount()).as("running stage is not terminated").isEqualTo(1);
		// non-blocking terminal: the submission settles when the marker is processed
		loop.submit(terminal(named("END"))).get(2, TimeUnit.SECONDS);
		assertThat(terminated.await(2, TimeUnit.SECONDS))
				.as("termination observed through the hook, fired in run()'s final block")
				.isTrue();
		assertThat(hook.get()).as("the hook receives the ended stage itself").isSameAs(loop);
	}

	@Test
	void failingTerminationHookIsContained() throws Exception {
		var hookRan = new CountDownLatch(1);
		var loop = new ActionProcessingLoop(daemonFactory(), TestContext.SILENT, DISCARD, stage -> {
			hookRan.countDown();
			throw new IllegalStateException("hook down");
		});
		loop.submit(terminal(named("END"))).get(2, TimeUnit.SECONDS);
		assertThat(hookRan.await(2, TimeUnit.SECONDS)).as("the hook ran and its failure was swallowed").isTrue();
		assertThatThrownBy(() -> loop.submit(named("late")))
				.as("the stage is terminal either way")
				.isInstanceOf(SubmitRejectedException.class);
	}

	@Test
	void capabilityInterfacesStayOffTheFuture() {
		// Cancelable is a capability, not a trait - like a command offering wind-down
		Cancelable victim = new Cancelable() { };
		Action action = Traits.copyTraits(victim, named("cap"));
		var future = loop().submit(action);
		assertThat(future)
				.as("Cancelable is the task's capability - never claimed by the future")
				.isNotInstanceOf(Cancelable.class);
	}

	@Test
	void submittedFutureCarriesNoTaskTraits() {
		var future = loop().submit(terminal(named("END")));
		assertThat(future).as("returned future claims none of the task's traits")
				.isNotInstanceOf(Shutdown.class)
				.isNotInstanceOf(Canceling.class);
	}

	// ---- terminal tasks and shutdown --------------------------------------------------

	@Test
	void terminalTaskEndsLoopAndRejectsLater() throws Exception {
		var loop = loop();
		loop.submit(named("first"));
		// non-blocking terminal marker: the future settles once the drain was processed
		var terminalAnswer = loop.submit(terminal(named("END"))).get(2, TimeUnit.SECONDS);
		assertThat(terminalAnswer)
				.as("the settled result is the trait-copied answer of the terminal action")
				.isInstanceOf(Shutdown.class);
		assertThat(terminalAnswer.format()).isNull();
		assertThat(processed).containsExactly("first", "END");
		assertStopped(loop);
	}

	@Test
	void terminalMarkerDrainsQueueAndClosesAcceptance() throws Exception {
		var loop = loop();
		loop.submit(named("a"));
		loop.submit(named("b"));
		// shutdown-by-submit: the caller builds the Shutdown marker itself; submission
		// returns as soon as it is accepted and the marker settles when its drain ran
		loop.submit(terminal(named("STOP"))).get(2, TimeUnit.SECONDS);
		assertThat(processed).as("everything accepted before the marker runs first")
				.containsExactly("a", "b", "STOP");
		assertThatThrownBy(() -> loop.submit(terminal(named("STOP2"))))
				.as("a second marker is refused like any later task - callers swallow it")
				.isInstanceOf(SubmitRejectedException.class);
		assertStopped(loop);
	}

	@Test
	void terminalSubmissionDoesNotBlockTheSubmitter() throws Exception {
		var started = new CountDownLatch(1);
		var release = new CountDownLatch(1);
		var loop = loop();
		Action blocker = context -> {
			started.countDown();
			try {
				release.await(2, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			return Result.IGNORED;
		};
		loop.submit(blocker);
		assertThat(started.await(2, TimeUnit.SECONDS)).isTrue();
		// the accepted-and-last marker returns right away, the drain is still running
		var marker = loop.submit(terminal(named("STOP")));
		assertThat(release.getCount()).as("nothing waited for the drain").isEqualTo(1);
		release.countDown();
		assertThat(marker.get(2, TimeUnit.SECONDS)).isInstanceOf(Shutdown.class);
	}

	@Test
	void rejectedSubmissionIsNeverProcessed() throws Exception {
		var loop = loop();
		loop.submit(terminal(named("END"))).get(2, TimeUnit.SECONDS);
		assertThatThrownBy(() -> loop.submit(named("late")))
				.isInstanceOf(SubmitRejectedException.class);
		assertThat(processed)
				.as("a refused submission never enters the queue, so its body never runs")
				.containsExactly("END");
	}

	// ---- failure paths ----------------------------------------------------------------

	@Test
	void bodyFailureIsAnsweredAndTheLoopSurvives() throws Exception {
		var loop = loop();
		assertThat(loop.submit(context -> {
			throw new IllegalStateException("boom");
		}).get(2, TimeUnit.SECONDS))
				.as("the failure replacement settles the future normally")
				.isInstanceOfSatisfying(Result.class, answer ->
						assertThat(answer.format()).isEqualTo("? boom\n"));
		assertThat(loop.submit(named("good")).get(2, TimeUnit.SECONDS)).isSameAs(Result.IGNORED);
	}

	@Test
	void nullResultIsReplacedByIgnored() throws Exception {
		var loop = loop();
		// the Action contract promises a non-null result; a violation is answered, not fatal
		assertThat(loop.submit(context -> (Result) null).get(2, TimeUnit.SECONDS))
				.isSameAs(Result.IGNORED);
		assertThat(loop.submit(named("good")).get(2, TimeUnit.SECONDS)).isSameAs(Result.IGNORED);
	}

	@Test
	void errorSettlesTaskAndCancelsQueuedWork() throws Exception {
		var started = new CountDownLatch(1);
		var hold = new CountDownLatch(1);
		var loop = loop();
		Action fatal = context -> {
			started.countDown();
			try {
				hold.await(2, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			throw new StackOverflowError("fatal");
		};
		var doomed = loop.submit(fatal);
		assertThat(started.await(2, TimeUnit.SECONDS)).isTrue();
		var orphaned = loop.submit(named("orphan")); // queued behind the running fatal task
		hold.countDown();

		assertThatThrownBy(() -> doomed.get(2, TimeUnit.SECONDS))
				.as("error settles its own future before the thread dies")
				.isInstanceOf(ExecutionException.class)
				.hasCauseInstanceOf(StackOverflowError.class);
		assertThatThrownBy(() -> orphaned.get(2, TimeUnit.SECONDS))
				.as("accepted-but-abandoned work is cancelled by the closing block, no submitter hangs")
				.isInstanceOf(CancellationException.class);
		assertStopped(loop);
	}

	@Test
	void rejectedExecutionFromBodyIsAnOrdinaryFailureAndTheStageSurvives() throws Exception {
		var loop = loop();
		// only the stage's own hand-off can prove its sink is gone; a foreign rejection
		// escaping a body is just another runtime failure (dead-sink cascade is pinned
		// by ActionProcessingLoopTest#deadDownstreamEndsThisStage)
		assertThat(loop.submit(context -> {
			throw new RejectedExecutionException("some executor is done");
		}).get(2, TimeUnit.SECONDS))
				.isInstanceOfSatisfying(Result.class, answer ->
						assertThat(answer.format()).isEqualTo("? some executor is done\n"));
		assertThat(loop.submit(named("good")).get(2, TimeUnit.SECONDS)).isSameAs(Result.IGNORED);
	}

	@Test
	void downstreamRefusalEndsTheLoopAndFailsTheHandedOffTask() throws Exception {
		var terminations = new CountDownLatch(1);
		var downstream = new ExecutionLoop<Result, Result>() {

			@Override
			public Future<Result> submit(Result task) {
				throw new SubmitRejectedException("sink gone");
			}

		};
		var loop = new ActionProcessingLoop(daemonFactory(), TestContext.SILENT, downstream,
				stage -> terminations.countDown());
		var refused = loop.submit(named("doomed"));
		assertThatThrownBy(() -> refused.get(2, TimeUnit.SECONDS))
				.as("a result that can never be handed off fails its future")
				.isInstanceOf(ExecutionException.class)
				.hasCauseInstanceOf(SubmitRejectedException.class);
		assertThat(terminations.await(2, TimeUnit.SECONDS))
				.as("no stage outlives its sink")
				.isTrue();
	}

	// ---- interrupt policy ---------------------------------------------------------------

	@Test
	void strayInterruptBelongsToNoTaskAndDoesNotKillLoop() throws Exception {
		var loop = loop();
		// the flag is left set by the body that did not clean up after itself
		assertThat(loop.submit(context -> {
			Thread.currentThread().interrupt();
			return Result.IGNORED;
		}).get(2, TimeUnit.SECONDS)).isSameAs(Result.IGNORED);
		assertThat(loop.submit(named("next")).get(2, TimeUnit.SECONDS)).isSameAs(Result.IGNORED);
	}

}
