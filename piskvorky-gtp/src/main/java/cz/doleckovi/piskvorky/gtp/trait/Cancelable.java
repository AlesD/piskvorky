package cz.doleckovi.piskvorky.gtp.trait;

import cz.doleckovi.piskvorky.gtp.action.Action;

/** Task that can be canceled by {@link Canceling} task.
 * <p>Not a {@link Trait}: {@link #cancel()} is behavior, and its
 * implementation belongs to the object declaring the interface. The
 * execution layer takes care of calling cancel.</p>
 * <p>Canceling is fully cooperative: {@code cancel()} runs on the submitting
 * thread and can never be told the victim's worker, so its only mechanism is
 * provider-owned state the body itself watches - a flag the body polls on its
 * way through the work, or a release of a latch or condition the body waits on
 * (a provider that waits may record its worker when {@code execute} begins,
 * but it must then consume any interrupt it targets at that worker before the
 * body returns). The execution stage never interrupts its worker and has no
 * watchdog for the request: a body that does not notice holds the stage - an
 * unresponsive victim is a provider bug, not a pipeline concern.</p> */
public interface Cancelable {

    /** Polite request abandon current execution.
     * <p>This request is called from thread submitting {@link Canceling} action.</p>
     * @return {@code true} if the thread executing {@link Cancelable} should
     *         be interrupted
     */
    default boolean cancelDuringExecution() {
        return true;
    }

    /** Notification about being cancelled while waiting in queue.
     * <p>This request is called from the execution thread just before
     * {@link Action#execute}. Cancellable actions should take precautions to
     * make execution fast. Default implementation interrupts calling thread so
     * it will be interrupted from the very beginning of executinon.</p>
     */
    default void cancelBeforeExecution() {
        Thread.currentThread().interrupt();
    }

}
