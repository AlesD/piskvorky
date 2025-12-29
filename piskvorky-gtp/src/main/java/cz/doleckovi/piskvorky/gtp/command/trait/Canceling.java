package cz.doleckovi.piskvorky.gtp.command.trait;

import java.util.concurrent.Future;

/** Operation that cancels any {@link Cancelable} operations passed for execution. */
public interface Canceling {

    /** Notification about cancelling of <strong>submitted</strong> operation.
     * <p>This method is called only for {@code cancelable} operation which was executing when {@code this} was queued.
     * If the executing operation was not {@code Cancelable} or if the executor was idle, then this method won't be
     * called. Submitted {@code Cancelable} operations in executor queue will not execute and this method will not be
     * called for them.</p>
     * <p>Note that {@code cancellable} operation might decide to ignore the cancel. In that case the
     * {@link Future#isDone} will return {@code false}. Default implementation will call {@code future.cancel(true)} to
     * force the cancellation.</p>
     * @param cancelable {@link Cancelable} canceled with {@code this} instance
     * @param future Future of {@code cancelable} operation
     */
    default void canceled(Cancelable cancelable, Future<?> future) {
        if (!future.isDone())
            future.cancel(true);
    }

    /** Notification about cancelling of operations which were <strong>NOT submitted</strong>.
     * <p>This method is called only for {@code cancelable} operation which was executing when {@code this} was queued.
     * If the executing operation was not {@code Cancelable} or if the executor was idle, then this method won't be
     * called. Other NOT submitted {@code Cancelable} operations in executor will still execute.</p>
     * @param cancelable {@link Cancelable} canceled with {@code this} instance
     * @param cancellationResult Object returned by {@code cancelable}
     * @param cancellationFailure Exception thrown by {@code cancelable}
     */
    default void canceled(Cancelable cancelable, Object cancellationResult, Exception cancellationFailure) {
        // Do nothing
    }

}
