package cz.doleckovi.piskvorky.gtp.execution;

import cz.doleckovi.piskvorky.gtp.action.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/** Test downstream that does nothing and never rejects.
 * <p>Each task is settled with itself on the submitting thread, mirroring what
 * a healthy execution loop hands back</p>. */
public final class DiscardingResultSink implements ExecutionLoop<Result, Result> {

	@Override
	public Future<Result> submit(Result task) {
		return CompletableFuture.completedFuture(task);
	}

}
