package cz.doleckovi.piskvorky.gtp.execution;

import cz.doleckovi.piskvorky.gtp.trait.Shutdown;

import java.util.concurrent.Future;

/** Execution pipeline stage.
 * <p>Loop consumes tasks and produces results. Submission is the only
 * operation exposed. Tasks with {@link Shutdown} trait terminate the stage and
 * every later {@link #submit} answers with {@link SubmitRejectedException}.
 * Loop can terminate <em>spontaneously</em>. Rejected submit is the only way
 * how caller can detect that the stage is terminated.</p>
 * <p>Implementation classes are encouraged to take callback that the loop can
 * use to notify about its execution.</p>
 */
public interface ExecutionLoop<T, R> {

	/** Submits a task for processing by this stage.
	 * @param task unit of work
	 * @return future for the task's outcome, settled when the stage has
	 *         processed the task
	 * @throws NullPointerException if the task is {@code null}
	 * @throws SubmitRejectedException if the stage does not accept tasks
	 */
	Future<R> submit(T task) throws SubmitRejectedException;

}
