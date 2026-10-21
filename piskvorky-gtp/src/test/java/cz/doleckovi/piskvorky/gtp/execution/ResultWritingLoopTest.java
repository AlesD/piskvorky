package cz.doleckovi.piskvorky.gtp.execution;

import cz.doleckovi.piskvorky.gtp.action.Result;
import cz.doleckovi.piskvorky.gtp.trait.Canceling;
import cz.doleckovi.piskvorky.gtp.trait.Shutdown;
import cz.doleckovi.piskvorky.gtp.trait.Traits;
import cz.doleckovi.piskvorky.gtp.transport.LineWriter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

/**
 * Covers the {@link ResultWritingLoop} dispatch stage: 1:1 ordered writing of
 * formatted results (multi-line blocks kept whole, {@link Result#IGNORED} not
 * written), the {@link Shutdown} terminalization protocol (accepted-and-last,
 * later submissions refused, ended stage delivered to the hook once on the
 * worker), the broken-writer path (failed write settles its future
 * exceptionally and the stage injects its own shutdown marker behind the
 * already accepted work, which then drains to the marker), {@link Error}s
 * ending the worker with the accepted-but-never-run queue cancelled, the bare
 * submission futures and the interrupt of a parked worker
 * (TODO.md [7] pipeline mechanics).
 */
class ResultWritingLoopTest {

	// ---- result fixtures -----------------------------------------------------------------

	private static final Result ALPHA = new Result.Success("1", "alpha\nbeta");
	private static final Result BRAVO = new Result.Failure("2", "bad move");
	private static final Result CHARLIE = new Result.Success(null, "last");

	/** The drain marker as the stage itself mints it: {@code IGNORED} claiming {@link Shutdown}. */
	private static Result shutdownMarker() {
		return Traits.addTraits(Result.IGNORED, Shutdown.class);
	}

	// ---- writer fixture -------------------------------------------------------------------

	/**
	 * Recording {@link LineWriter}: captures every block written, can park one call on
	 * a gate (so the test can enqueue work while the worker is mid-write and shape
	 * deterministic interleavings) and fail calls afterwards. A parked call records the
	 * text <em>before</em> blocking and failing, so {@link #written} shows attempts.
	 */
	private static final class TestWriter implements LineWriter {
		final List<String> written = new CopyOnWriteArrayList<>();
		final AtomicInteger calls = new AtomicInteger();
		private final CountDownLatch parkEntered = new CountDownLatch(1);
		private final AtomicBoolean failureConsumed = new AtomicBoolean();
		private volatile int parkOnCall = -1;
		private volatile CountDownLatch parkRelease = new CountDownLatch(0);
		private volatile Throwable failure;
		private volatile boolean failFirstCallOnly;

		void parkNextCall() {
			parkRelease = new CountDownLatch(1);
			parkOnCall = calls.get() + 1;
		}

		void awaitParked() {
			try {
				assertThat(parkEntered.await(2, TimeUnit.SECONDS)).as("worker reached the gate").isTrue();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				fail("interrupted while awaiting the gate");
			}
		}

		void releasePark() {
			parkRelease.countDown();
		}

		/** The armed call (and every later one, unless {@code onlyFirst}) rethrows {@code failure}. */
		void failWith(Throwable failure, boolean onlyFirstCall) {
			this.failure = failure;
			this.failFirstCallOnly = onlyFirstCall;
		}

		@Override
		public void writeln(String text) throws IOException {
			int n = calls.incrementAndGet();
			written.add(text);
			if (n == parkOnCall) {
				parkEntered.countDown();
				try {
					assertThat(parkRelease.await(2, TimeUnit.SECONDS)).as("gate released").isTrue();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			var f = failure;
			if (f != null && (!failFirstCallOnly || !failureConsumed.getAndSet(true))) {
				if (f instanceof IOException e)
					throw e;
				if (f instanceof RuntimeException e)
					throw e;
				if (f instanceof Error e)
					throw e;
				throw new IOException("unexpected fixture failure type", f);
			}
		}
	}

	// ---- wiring helpers --------------------------------------------------------------------

	private static final ThreadFactory DAEMON_FACTORY = runnable -> {
		var thread = new Thread(runnable, "result-writing-test-worker");
		thread.setDaemon(true);
		return thread;
	};

	/** A stage plus the wiring-side view of its end: the termination latch, hook payload,
	 * hook call count and hook thread. */
	private static final class Stage {
		final TestWriter writer = new TestWriter();
		final ResultWritingLoop loop;
		final CountDownLatch terminated = new CountDownLatch(1);
		final AtomicInteger hookCalls = new AtomicInteger();
		final AtomicReference<ExecutionLoop<Result, Result>> endedStage = new AtomicReference<>();
		final AtomicReference<Thread> hookThread = new AtomicReference<>();
		private final AtomicReference<Thread> workerThread = new AtomicReference<>();

		Stage() {
			ThreadFactory namingFactory = runnable -> {
				var thread = DAEMON_FACTORY.newThread(runnable);
				workerThread.set(thread);
				return thread;
			};
			loop = new ResultWritingLoop(namingFactory, writer, stage -> {
				hookCalls.incrementAndGet();
				endedStage.set(stage);
				hookThread.set(Thread.currentThread());
				terminated.countDown();
			});
		}

		void awaitTermination() {
			try {
				assertThat(terminated.await(2, TimeUnit.SECONDS)).as("worker exited").isTrue();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				fail("interrupted while awaiting");
			}
		}

		void interruptWorker() {
			workerThread.get().interrupt();
		}
	}

	private static Result get(Future<Result> future) throws Exception {
		return future.get(2, TimeUnit.SECONDS);
	}

	// ---- ordered writing -------------------------------------------------------------------

	@Test
	void resultsAreWrittenInOrderAndBlocksStayWhole() throws Exception {
		var stage = new Stage();
		var alpha = stage.loop.submit(ALPHA);
		var bravo = stage.loop.submit(BRAVO);
		var ignored = stage.loop.submit(Result.IGNORED);
		var charlie = stage.loop.submit(CHARLIE);
		// each future settles only after its write happened, so settling them in
		// submission order proves the writer saw exactly that order
		assertThat(get(alpha)).isSameAs(ALPHA);
		assertThat(get(bravo)).isSameAs(BRAVO);
		assertThat(get(ignored)).isSameAs(Result.IGNORED);
		assertThat(get(charlie)).isSameAs(CHARLIE);
		assertThat(stage.writer.written)
				.as("IGNORED has no wire form, multi-line answers travel as one atomic block")
				.containsExactly("= 1 alpha\nbeta\n", "? 2 bad move\n", "= last\n");
		stage.loop.submit(shutdownMarker());
		stage.awaitTermination();
	}

	@Test
	void nullCollaboratorsAreRejectedAtAssembly() {
		assertThatThrownBy(() -> new ResultWritingLoop(null, new TestWriter(), stage -> { }))
				.isInstanceOf(NullPointerException.class);
		assertThatThrownBy(() -> new ResultWritingLoop(DAEMON_FACTORY, null, stage -> { }))
				.isInstanceOf(NullPointerException.class);
	}

	@Test
	void nullSubmissionIsRejected() {
		var stage = new Stage();
		assertThatThrownBy(() -> stage.loop.submit(null)).isInstanceOf(NullPointerException.class);
		stage.loop.submit(shutdownMarker());
		stage.awaitTermination();
	}

	// ---- Shutdown protocol -----------------------------------------------------------------

	@Test
	void shutdownIsAcceptedLastAndTerminalizesTheStage() throws Exception {
		var stage = new Stage();
		var alpha = stage.loop.submit(ALPHA);
		var markerResult = shutdownMarker();
		var marker = stage.loop.submit(markerResult);
		assertThatThrownBy(() -> stage.loop.submit(BRAVO))
				.as("terminality is observable as the documented rejection")
				.isInstanceOf(SubmitRejectedException.class);
		assertThat(get(alpha)).isSameAs(ALPHA);
		assertThat(get(marker))
				.as("the marker is IGNORED: settled normally with itself, never written")
				.isSameAs(markerResult)
				.isInstanceOf(Shutdown.class);
		assertThat(markerResult.format()).isNull();
		stage.awaitTermination();
		assertThat(stage.writer.written).containsExactly(ALPHA.format());
	}

	@Test
	void submissionFuturesCarryNoTraits() throws Exception {
		var stage = new Stage();
		assertThat(stage.loop.submit(ALPHA))
				.as("an untraited result yields the bare future")
				.isNotInstanceOf(Shutdown.class)
				.isNotInstanceOf(Canceling.class);
		assertThat(stage.loop.submit(shutdownMarker()))
				.as("the future stays bare even for a traited task")
				.isNotInstanceOf(Shutdown.class)
				.isNotInstanceOf(Canceling.class);
		stage.awaitTermination();
	}

	@Test
	void terminationHookFiresOnceOnTheExitingWorker() {
		var stage = new Stage();
		assertThat(stage.hookCalls.get()).as("running stage is not terminated").isZero();
		stage.loop.submit(shutdownMarker());
		stage.awaitTermination();
		assertThat(stage.endedStage.get()).as("the hook receives the ended stage itself").isSameAs(stage.loop);
		assertThat(stage.hookCalls.get()).as("exactly once").isEqualTo(1);
		assertThat(stage.hookThread.get().getName())
				.as("delivered on the worker thread")
				.isEqualTo("result-writing-test-worker");
	}

	@Test
	void interruptingTheParkedWorkerEndsTheStage() {
		var stage = new Stage();
		stage.interruptWorker(); // the worker idles in await(): the flag makes it throw
		stage.awaitTermination();
		assertThat(stage.endedStage.get()).isSameAs(stage.loop);
		assertThat(stage.writer.written).isEmpty();
	}

	// ---- broken writer -----------------------------------------------------------------------

	@Test
	void ioFailureTerminalizesButAcceptedWorkStillDrains() throws Exception {
		// park the worker inside ALPHA's write so BRAVO is provably accepted before the
		// failure path injects its marker - the queue then holds [BRAVO, marker]
		var stage = new Stage();
		stage.writer.parkNextCall();
		var alpha = stage.loop.submit(ALPHA);
		stage.writer.awaitParked();
		var bravo = stage.loop.submit(BRAVO);
		var failure = new IOException("pipe down");
		stage.writer.failWith(failure, true);
		stage.writer.releasePark();
		assertThatThrownBy(() -> get(alpha))
				.isInstanceOf(ExecutionException.class)
				.extracting(Throwable::getCause).isSameAs(failure);
		assertThat(get(bravo))
				.as("the write after the failure still happens - drain runs to the marker")
				.isSameAs(BRAVO);
		stage.awaitTermination();
		assertThat(stage.writer.written).containsExactly(ALPHA.format(), BRAVO.format());
		assertThat(stage.hookCalls.get()).isEqualTo(1);
		assertThatThrownBy(() -> stage.loop.submit(CHARLIE)).isInstanceOf(SubmitRejectedException.class);
	}

	@Test
	void runtimeFailureIsTreatedLikeAWriterFailure() throws Exception {
		var stage = new Stage();
		stage.writer.parkNextCall();
		var alpha = stage.loop.submit(ALPHA);
		stage.writer.awaitParked();
		var bravo = stage.loop.submit(BRAVO);
		var failure = new IllegalStateException("corrupted channel");
		stage.writer.failWith(failure, true);
		stage.writer.releasePark();
		assertThatThrownBy(() -> get(alpha))
				.isInstanceOf(ExecutionException.class)
				.extracting(Throwable::getCause).isSameAs(failure);
		assertThat(get(bravo)).isSameAs(BRAVO);
		stage.awaitTermination();
		assertThat(stage.writer.written).containsExactly(ALPHA.format(), BRAVO.format());
	}

	@Test
	void persistentlyBrokenWriterSettlesEveryAcceptedResultExceptionally() throws Exception {
		// all three results are enqueued while the worker is parked in the first write,
		// so all three are accepted before the first failure terminalizes the stage
		var stage = new Stage();
		stage.writer.parkNextCall();
		var alpha = stage.loop.submit(ALPHA);
		stage.writer.awaitParked();
		var bravo = stage.loop.submit(BRAVO);
		var charlie = stage.loop.submit(CHARLIE);
		var failure = new IOException("destination gone");
		stage.writer.failWith(failure, false);
		stage.writer.releasePark();
		assertThatThrownBy(() -> get(alpha))
				.isInstanceOf(ExecutionException.class)
				.extracting(Throwable::getCause).isSameAs(failure);
		assertThatThrownBy(() -> get(bravo))
				.as("drained behind the failed write, each attempt fails the same way")
				.isInstanceOf(ExecutionException.class)
				.extracting(Throwable::getCause).isSameAs(failure);
		assertThatThrownBy(() -> get(charlie))
				.isInstanceOf(ExecutionException.class)
				.extracting(Throwable::getCause).isSameAs(failure);
		stage.awaitTermination();
		assertThat(stage.writer.written).containsExactly(ALPHA.format(), BRAVO.format(), CHARLIE.format());
	}

	@Test
	void errorEndsWorkerAndCancelsAcceptedUnrunWork() throws Exception {
		var stage = new Stage();
		stage.writer.parkNextCall();
		var alpha = stage.loop.submit(ALPHA);
		stage.writer.awaitParked();
		var bravo = stage.loop.submit(BRAVO);
		var failure = new StackOverflowError("simulated");
		stage.writer.failWith(failure, true);
		stage.writer.releasePark();
		assertThatThrownBy(() -> get(alpha))
				.isInstanceOf(ExecutionException.class)
				.extracting(Throwable::getCause).isSameAs(failure);
		assertThat(bravo)
				.as("accepted before the exit but never dispatched: cancelled, not failed")
				.isCancelled();
		stage.awaitTermination();
		assertThat(stage.writer.written).containsExactly(ALPHA.format());
		assertThat(stage.hookCalls.get()).isEqualTo(1);
	}

	@Test
	void failingTerminalWriteStillEndsTheStage() throws Exception {
		// the quit answer is the last thing the engine ever writes - and that write fails:
		// the stage must still end, or the owning session waits for a worker that parked
		// on an empty queue forever
		var stage = new Stage();
		var failure = new IOException("destination gone");
		stage.writer.failWith(failure, true);
		var terminal = Traits.addTraits((Result) new Result.Success("9", null), Shutdown.class);
		var marker = stage.loop.submit(terminal);
		assertThatThrownBy(() -> get(marker))
				.as("the unwritable answer fails its own future")
				.isInstanceOf(ExecutionException.class)
				.extracting(Throwable::getCause).isSameAs(failure);
		stage.awaitTermination();
		assertThat(stage.hookCalls.get()).isEqualTo(1);
		assertThatThrownBy(() -> stage.loop.submit(ALPHA))
				.as("the stage is terminal though its last write failed")
				.isInstanceOf(SubmitRejectedException.class);
	}

}
