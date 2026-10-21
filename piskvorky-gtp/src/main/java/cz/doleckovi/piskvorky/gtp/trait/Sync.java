package cz.doleckovi.piskvorky.gtp.trait;

/** Implemented by tasks whose submission must not return before the receiving stage
 * has fully processed the task.
 *
 * <p>Whether the marker is honored is a property of the stage: the execution stage
 * ({@link cz.doleckovi.piskvorky.gtp.execution.ActionProcessingLoop}) waits for a
 * {@code Sync} action's execution <em>and</em> the dispatch of its result before its
 * {@code submit} returns; the reading head and the dispatch stage accept {@code Sync}
 * tasks without blocking. A {@code Sync} task must never be submitted from the
 * receiving stage's own worker thread (it would wait for itself).</p>
 *
 * <p>{@link Shutdown} deliberately does <em>not</em> extend this marker: terminal
 * submissions return as soon as the marker is accepted, and the completed drain is
 * observed through the stages' termination hooks instead of a blocked submitter.</p>
 */
public interface Sync extends Trait {
}
