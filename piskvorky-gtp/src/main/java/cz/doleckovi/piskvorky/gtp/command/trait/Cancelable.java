package cz.doleckovi.piskvorky.gtp.command.trait;

import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;

/** Operation that can be canceled by {@link Canceling} operation. */
public interface Cancelable {

    /** Polite request for cancellation.
     * <p>This operation is called only if execution of {@code this} already started and is not finished. The call is
     * made from different thread so keep the implementation thread safe.</p>
     * <p>Default implementation calls {@code if (future != null) future.cancel(false)}. Implementors might override
     * this behavior. They may for example finish execution with partial result they already have. In that case they
     * should make sure that the future {@link Future#isDone() is done} before returning, because cancelling operations
     * are notified using {@link Canceling#canceled} which, by default, call {@code future.cancel(true)} on futures
     * that are not done.</p>
     * @param canceling First cancelling operation submitted after execution start
     * @param future Future of this operation if was queued for execution using {@link ExecutorService#submit} or
     *               {@code null} if it was queued for execution using {@link ExecutorService#execute}
     */
    default void cancel(Canceling canceling, Future<?> future) {
        future.cancel(false);
    }

    default Object cancel(Canceling canceling) throws Exception {
        return null;
    }

}
