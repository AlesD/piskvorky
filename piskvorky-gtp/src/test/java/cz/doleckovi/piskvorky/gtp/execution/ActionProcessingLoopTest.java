package cz.doleckovi.piskvorky.gtp.execution;

import cz.doleckovi.piskvorky.gtp.action.Action;
import cz.doleckovi.piskvorky.gtp.action.Context;
import cz.doleckovi.piskvorky.gtp.action.TestContext;
import cz.doleckovi.piskvorky.gtp.action.Result;
import cz.doleckovi.piskvorky.gtp.trait.Cancelable;
import cz.doleckovi.piskvorky.gtp.trait.Canceling;
import cz.doleckovi.piskvorky.gtp.trait.Shutdown;
import cz.doleckovi.piskvorky.gtp.trait.Sync;
import cz.doleckovi.piskvorky.gtp.trait.Traits;
import cz.doleckovi.piskvorky.gtp.transport.LineWriter;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

/**
 * Covers the {@link ActionProcessingLoop} stage contract: the hand-off (every produced
 * result is dispatched to the downstream stage, traits propagate through the result so
 * {@code Shutdown} cascades reach the downstream, a downstream that refuses work ends
 * this stage as well, and a failed action's replacement is dispatched like a normal
 * result), this stage's exclusive interpretation of {@code Sync} (submission waits for
 * execution <em>and</em> dispatch, TODO.md [7]) and of {@code Canceling} (running and
 * queued {@link Cancelable} actions are asked to wind down - cooperatively, or with the
 * interrupt the provider requests, TODO.md [28]), while
 * the dispatch stage interprets neither.
 */
class ActionProcessingLoopTest {

	// ---- task and sink fixtures --------------------------------------------------------

	private record StubAction(Result outcome) implements Action {
		@Override
		public Result execute(Context context) {
			return outcome;
		}
	}

	/** Like {@code quit} used to be: Sync + Shutdown, submission waits for dispatch. */
	private record TerminalAction(Result outcome) implements Action, Sync, Shutdown {
		@Override
		public Result execute(Context context) {
			return outcome;
		}
	}

	/** What {@code quit} is now: terminal, but the submission never blocks. */
	private record AsyncTerminalAction(Result outcome) implements Action, Shutdown {
		@Override
		public Result execute(Context context) {
			return outcome;
		}
	}

	/** Non-terminal Sync task: submission blocks until the result has been dispatched. */
	private record SyncAction(Result outcome) implements Action, Sync {
		@Override
		public Result execute(Context context) {
			return outcome;
		}
	}

	/** A {@code Canceling} action, like {@code # interrupt} / SIGINT / EOF marker. */
	private record CancelerAction() implements Action, Canceling {
		@Override
		public Result execute(Context context) {
			return Result.IGNORED;
		}
	}

	/**
	 * Cancelable action whose body parks until it is released - either by the provider
	 * itself (cooperative wind-down) or by the interrupt the stage delivers when the
	 * provider asks for one. {@link #stubborn()} answers neither: it keeps holding the
	 * worker until the test releases {@link #release}.
	 */
	private static final class ParkedAction implements Action, Cancelable {

		final CountDownLatch started = new CountDownLatch(1);
		final CountDownLatch release = new CountDownLatch(1);
		final AtomicInteger duringExecutionCalls = new AtomicInteger();
		final AtomicInteger beforeExecutionCalls = new AtomicInteger();
		final AtomicBoolean sawInterrupt = new AtomicBoolean();
		/** Whether the wind-down releases the parked body itself. */
		private final boolean windDownOnCancel;
		/** Whether the stage is asked to interrupt its worker on the provider's behalf. */
		private final boolean asksForInterrupt;

		private ParkedAction(boolean windDownOnCancel, boolean asksForInterrupt) {
			this.windDownOnCancel = windDownOnCancel;
			this.asksForInterrupt = asksForInterrupt;
		}

		/** Cooperative victim: releases its own body, never asks for an interrupt. */
		static ParkedAction cooperative() {
			return new ParkedAction(true, false);
		}

		/** Interrupt-assisted victim: asks the stage to interrupt the worker. */
		static ParkedAction interruptAssisted() {
			return new ParkedAction(false, true);
		}

		/** Ignores the wind-down request; only the test releases it. */
		static ParkedAction stubborn() {
			return new ParkedAction(false, false);
		}

		@Override
		public Result execute(Context context) {
			started.countDown();
			try {
				release.await(2, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				sawInterrupt.set(true); // the flag is cleared by the throw
			}
			return Result.IGNORED;
		}

		@Override
		public boolean cancelDuringExecution() {
			duringExecutionCalls.incrementAndGet();
			if (windDownOnCancel)
				release.countDown(); // cooperative wind-down, no interrupt involved
			return asksForInterrupt;
		}

		@Override
		public void cancelBeforeExecution() {
			beforeExecutionCalls.incrementAndGet();
			if (asksForInterrupt)
				Cancelable.super.cancelBeforeExecution(); // default: body starts interrupted
			else if (windDownOnCancel)
				release.countDown(); // the body is not going to park at all
		}

		private int cancelCalls() {
			return duringExecutionCalls.get() + beforeExecutionCalls.get();
		}
	}

	private static ThreadFactory daemonFactory() {
		return runnable -> {
			var thread = new Thread(runnable);
			thread.setDaemon(true);
			return thread;
		};
	}

	private final List<String> blocks = new CopyOnWriteArrayList<>();

	private ResultWritingLoop processor() {
		return new ResultWritingLoop(daemonFactory(), blocks::add, stage -> { });
	}

	private static Action success(String id, String answer) {
		return new StubAction(new Result.Success(id, answer));
	}

	// ---- dispatch ------------------------------------------------------------------------

	@Test
	void resultsAreDispatchedToDownstream() throws Exception {
		var loop = new ActionProcessingLoop(daemonFactory(), TestContext.SILENT, processor(), stage -> { });
		// Sync action: submit returns only after the result has been dispatched
		loop.submit(new SyncAction(new Result.Success("1", "hi"))).get(2, TimeUnit.SECONDS);
		assertThat(blocks).containsExactly("= 1 hi\n");
	}

	@Test
	void ignoredResultsWriteNothingButStillDispatch() throws Exception {
		var loop = new ActionProcessingLoop(daemonFactory(), TestContext.SILENT, processor(), stage -> { });
		loop.submit(new SyncAction(Result.IGNORED)).get(2, TimeUnit.SECONDS);
		assertThat(blocks).as("IGNORED carries no wire bytes").isEmpty();
	}

	@Test
	void discardSinkKeepsStageUsableWithoutDownstream() throws Exception {
		var loop = new ActionProcessingLoop(daemonFactory(), TestContext.SILENT, new DiscardingResultSink(), stage -> { });
		var result = loop.submit(success("7", "standalone")).get(2, TimeUnit.SECONDS);
		assertThat(result.format()).isEqualTo("= 7 standalone\n");
	}

	@Test
	void syncSubmitBlocksUntilExecutionAndDispatch() throws Exception {
		var entered = new CountDownLatch(1);
		var release = new CountDownLatch(1);
		var returned = new AtomicBoolean();
		LineWriter slow = line -> {
			entered.countDown();
			try {
				release.await(2, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		};
		var loop = new ActionProcessingLoop(daemonFactory(), TestContext.SILENT,
				new ResultWritingLoop(daemonFactory(), slow, stage -> { }), stage -> { });
		var submitter = new Thread(() -> {
			try {
				loop.submit(new SyncAction(new Result.Success("1", "hi"))).get(2, TimeUnit.SECONDS);
				returned.set(true);
			} catch (Exception e) {
				fail("sync submission failed", e);
			}
		});
		submitter.setDaemon(true);
		submitter.start();
		assertThat(entered.await(2, TimeUnit.SECONDS)).as("result reached the slow writer").isTrue();
		Thread.sleep(100);
		assertThat(returned).as("Sync submit chains: execution, then dispatch").isFalse();
		release.countDown();
		submitter.join(2000);
		assertThat(returned).as("Sync submit returned after the answer was dispatched").isTrue();
	}

	// ---- trait cascade -------------------------------------------------------------------

	@Test
	void terminalResultAlsoTerminalizesDownstream() throws Exception {
		var downstream = processor();
		var loop = new ActionProcessingLoop(daemonFactory(), TestContext.SILENT, downstream, stage -> { });
		var terminal = new TerminalAction(new Result.Success(null, null));
		// explicitly Sync on top of Shutdown: this stage waits for dispatch
		assertThat(loop.submit(terminal).get(2, TimeUnit.SECONDS).format()).isEqualTo("=\n");
		assertThat(blocks).containsExactly("=\n");
		assertThatThrownBy(() -> loop.submit(success("2", "after")))
				.as("executor terminalized by its own task").isInstanceOf(SubmitRejectedException.class);
		assertThatThrownBy(() -> downstream.submit(Result.IGNORED))
				.as("processor terminalized by the cascaded result").isInstanceOf(SubmitRejectedException.class);
	}

	@Test
	void nonSyncTerminalCascadesWithoutBlockingTheSubmitter() throws Exception {
		var dispatchEnded = new CountDownLatch(1);
		var downstream = new ResultWritingLoop(daemonFactory(), blocks::add, stage -> dispatchEnded.countDown());
		var loop = new ActionProcessingLoop(daemonFactory(), TestContext.SILENT, downstream, stage -> { });
		// the real pipeline's quit: Shutdown alone - submission returns once handed off,
		// the dispatch completion is observed through the downstream's termination hook
		var future = loop.submit(new AsyncTerminalAction(new Result.Success(null, null)));
		assertThat(future.get(2, TimeUnit.SECONDS).format()).isEqualTo("=\n");
		assertThat(dispatchEnded.await(2, TimeUnit.SECONDS))
				.as("terminal result still reached the dispatch stage").isTrue();
		assertThat(blocks).containsExactly("=\n");
	}

	@Test
	void deadDownstreamEndsThisStage() throws Exception {
		var downstream = processor();
		// shutdown-by-submit: a Shutdown marker kills the sink's acceptance on the spot
		var marker = Traits.addTraits(Result.IGNORED, Shutdown.class);
		downstream.submit(marker); // acceptance flips at the moment of acceptance
		var loop = new ActionProcessingLoop(daemonFactory(), TestContext.SILENT, downstream, stage -> { });
		assertThatThrownBy(() -> loop.submit(success("1", "orphan")).get(2, TimeUnit.SECONDS))
				.as("result that can never be dispatched fails its submission")
				.isInstanceOf(ExecutionException.class)
				.hasCauseInstanceOf(SubmitRejectedException.class);
		// the refusal ends the loop: later probes are refused outright, or accepted just
		// before the exit and cancelled by its drain, or processed against the dead sink
		for (int attempt = 0; attempt < 100; ++attempt) {
			try {
				var probe = loop.submit(success("probe", "x"));
				try {
					probe.get(2, TimeUnit.SECONDS);
				} catch (CancellationException | ExecutionException settled) {
					return; // cancelled by the final drain, or refused by the dead sink
				}
			} catch (SubmitRejectedException refused) {
				return; // acceptance closed
			} catch (Exception raced) {
				// still queued behind the dying worker or timed out - keep probing
			}
			try {
				Thread.sleep(20);
			} catch (InterruptedException interrupted) {
				Thread.currentThread().interrupt();
				fail("interrupted while probing");
			}
		}
		fail("loop neither stopped accepting nor abandoned its probe");
	}

	// ---- replacement hand-off (TODO.md [28]) ---------------------------------------------

	/** Action body failing abnormally - the same escape {@code CommandAction} prevents. */
	private record ThrowingTerminalAction() implements Action, Sync, Shutdown {
		@Override
		public Result execute(Context context) {
			throw new IllegalStateException("kaboom");
		}
	}

	@Test
	void replacementResultIsDeliveredDownstream() throws Exception {
		var loop = new ActionProcessingLoop(daemonFactory(), TestContext.SILENT, processor(), stage -> { });
		// Sync so the submission settles only after the replacement has been dispatched
		var failing = Traits.addTraits((Action) context -> {
			throw new IllegalStateException("boom");
		}, Sync.class);
		assertThat(loop.submit(failing).get(2, TimeUnit.SECONDS))
				.as("replacement settles the future normally")
				.isInstanceOfSatisfying(Result.class, result -> {
					assertThat(result.format()).isEqualTo("? boom\n");
				});
		assertThat(blocks).as("the failure answer reaches the wire, not only the future")
				.containsExactly("? boom\n");
	}

	@Test
	void failedTerminalCarrierStillCascades() throws Exception {
		var downstream = processor();
		var loop = new ActionProcessingLoop(daemonFactory(), TestContext.SILENT, downstream, stage -> { });
		// a throwing quit: its trait-copied Failure replacement must still terminalize the
		// downstream - otherwise the dispatch worker would hang on a never-arriving marker
		assertThat(loop.submit(new ThrowingTerminalAction()).get(2, TimeUnit.SECONDS))
				.isInstanceOfSatisfying(Result.class, result -> {
					assertThat(result.format()).isEqualTo("? kaboom\n");
				});
		assertThat(blocks).containsExactly("? kaboom\n");
		assertThatThrownBy(() -> downstream.submit(Result.IGNORED))
				.as("terminalization cascaded through the replacement")
				.isInstanceOf(SubmitRejectedException.class);
		assertThatThrownBy(() -> loop.submit(success("2", "after")))
				.as("this stage terminalized by its own carrier, replacement failure notwithstanding")
				.isInstanceOf(SubmitRejectedException.class);
	}

	// ---- Canceling interpretation at this stage -------------------------------------------

	@Test
	void cancelingSubmissionWindsDownRunningCancelable() throws Exception {
		var loop = new ActionProcessingLoop(daemonFactory(), TestContext.SILENT, processor(), stage -> { });
		var victim = ParkedAction.cooperative(); // cancelDuringExecution() releases the parked body
		var pending = loop.submit(victim);
		assertThat(victim.started.await(2, TimeUnit.SECONDS)).isTrue();
		loop.submit(new CancelerAction()).get(2, TimeUnit.SECONDS);
		// nothing else releases the parked body - its completion proves the request reached it
		assertThat(pending.get(2, TimeUnit.SECONDS)).isSameAs(Result.IGNORED);
		assertThat(victim.duringExecutionCalls).as("running cancelable was asked to wind down").hasValue(1);
		assertThat(victim.sawInterrupt).as("the provider wound down without asking for an interrupt").isFalse();
	}

	@Test
	void cancelingSubmissionInterruptsWorkerWhenProviderAsksForIt() throws Exception {
		var loop = new ActionProcessingLoop(daemonFactory(), TestContext.SILENT, processor(), stage -> { });
		// the provider owns no state the body watches - it takes the offered interrupt
		var victim = ParkedAction.interruptAssisted();
		var pending = loop.submit(victim);
		assertThat(victim.started.await(2, TimeUnit.SECONDS)).isTrue();
		loop.submit(new CancelerAction()).get(2, TimeUnit.SECONDS);
		assertThat(victim.duringExecutionCalls).hasValue(1);
		assertThat(pending.get(2, TimeUnit.SECONDS))
				.as("the interrupt released the parked body")
				.isSameAs(Result.IGNORED);
		assertThat(victim.sawInterrupt).as("the body woke up on the interrupt the provider requested").isTrue();
		// the interrupt belonged to the canceled action: the stage outlives it
		assertThat(loop.submit(success("after", "x")).get(2, TimeUnit.SECONDS).format())
				.isEqualTo("= after x\n");
	}

	@Test
	void cancelingSubmissionCancelsQueuedCancelableBeforeExecution() throws Exception {
		// the CommandExecutorImplTest scenario on the pipeline: blocker running,
		// cancelable queued ahead of the carrier - the carrier's acceptance parks a pending
		// cancel that hits the victim in the locked pre-body window
		var loop = new ActionProcessingLoop(daemonFactory(), TestContext.SILENT, processor(), stage -> { });
		var blockerStarted = new CountDownLatch(1);
		var blockerRelease = new CountDownLatch(1);
		var blocker = (Action) context -> {
			blockerStarted.countDown();
			try {
				blockerRelease.await(2, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			return Result.IGNORED;
		};
		var victim = ParkedAction.cooperative();
		loop.submit(blocker);
		assertThat(blockerStarted.await(2, TimeUnit.SECONDS)).isTrue();
		var victimFuture = loop.submit(victim);
		var cancelerFuture = loop.submit(new CancelerAction());
		assertThat(victim.cancelCalls())
				.as("nothing canceled yet - blocker still holds the worker").isEqualTo(0);
		blockerRelease.countDown();
		cancelerFuture.get(2, TimeUnit.SECONDS);
		assertThat(victim.beforeExecutionCalls)
				.as("queued cancelable canceled before it started running").hasValue(1);
		assertThat(victim.duringExecutionCalls)
				.as("the running-window sweep never saw the queued victim").hasValue(0);
		assertThat(victimFuture.get(2, TimeUnit.SECONDS)).isSameAs(Result.IGNORED);
		assertThat(victim.sawInterrupt).isFalse();
	}

	@Test
	void queuedCancelableStartsInterruptedWhenProviderAsksForIt() throws Exception {
		// the same queue shape with a provider relying on the default
		// cancelBeforeExecution(), which interrupts the worker before the body runs
		var loop = new ActionProcessingLoop(daemonFactory(), TestContext.SILENT, processor(), stage -> { });
		var blockerStarted = new CountDownLatch(1);
		var blockerRelease = new CountDownLatch(1);
		var blocker = (Action) context -> {
			blockerStarted.countDown();
			try {
				blockerRelease.await(2, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			return Result.IGNORED;
		};
		var victim = ParkedAction.interruptAssisted();
		loop.submit(blocker);
		assertThat(blockerStarted.await(2, TimeUnit.SECONDS)).isTrue();
		var victimFuture = loop.submit(victim);
		var cancelerFuture = loop.submit(new CancelerAction());
		blockerRelease.countDown();
		cancelerFuture.get(2, TimeUnit.SECONDS);
		assertThat(victim.beforeExecutionCalls).hasValue(1);
		assertThat(victimFuture.get(2, TimeUnit.SECONDS))
				.as("the body ran interrupted and answered normally")
				.isSameAs(Result.IGNORED);
		assertThat(victim.sawInterrupt).isTrue();
	}

	@Test
	void repeatedCancelingSweepsTheSameVictimOnlyOnce() throws Exception {
		var loop = new ActionProcessingLoop(daemonFactory(), TestContext.SILENT, processor(), stage -> { });
		// stubborn victim: the wind-down request is ignored, so it keeps holding the
		// worker while both canceling carriers get accepted behind it
		var victim = ParkedAction.stubborn();
		var pending = loop.submit(victim);
		assertThat(victim.started.await(2, TimeUnit.SECONDS)).isTrue();
		var first = loop.submit(new CancelerAction());
		var second = loop.submit(new CancelerAction());
		assertThat(victim.duringExecutionCalls)
				.as("second acceptance sweep finds the victim already attempted").hasValue(1);
		victim.release.countDown();
		first.get(2, TimeUnit.SECONDS);
		second.get(2, TimeUnit.SECONDS);
		assertThat(pending.get(2, TimeUnit.SECONDS)).isSameAs(Result.IGNORED);
	}

	// ---- dispatch stage trait opt-outs (TODO.md [28]) --------------------------------------

	@Test
	void dispatchStageNeverCancelsTheRunningWriter() throws Exception {
		var entered = new CountDownLatch(1);
		var release = new CountDownLatch(1);
		var written = new CopyOnWriteArrayList<String>();
		LineWriter slow = line -> {
			written.add(line);
			entered.countDown();
			try {
				release.await(2, TimeUnit.SECONDS);
			} catch (InterruptedException interrupted) {
				Thread.currentThread().interrupt();
			}
		};
		var downstream = new ResultWritingLoop(daemonFactory(), slow, stage -> { });
		var cancelCalls = new AtomicInteger();
		Cancelable victimMarker = new Cancelable() {
			@Override
			public boolean cancelDuringExecution() {
				cancelCalls.incrementAndGet(); // would wind the dispatch worker down if honored
				return false;
			}
		};
		var victimResult = Traits.copyTraits(victimMarker, new Result.Success("1", "a"));
		downstream.submit(victimResult); // dispatch parks in the writer
		assertThat(entered.await(2, TimeUnit.SECONDS)).isTrue();
		var cancelingResult = Traits.addTraits(Result.IGNORED, Canceling.class);
		downstream.submit(cancelingResult); // accepted while the writer "runs"
		assertThat(cancelCalls)
				.as("Canceling is not interpreted at the dispatch stage").hasValue(0);
		release.countDown();
		// the blocked answer is written and the stage survived - waiting on the drain
		// submission's future proves it (Sync itself no longer blocks any submission)
		downstream.submit(Traits.addTraits(Result.IGNORED, Sync.class))
				.get(2, TimeUnit.SECONDS);
		assertThat(written).containsExactly("= 1 a\n");
	}

}
