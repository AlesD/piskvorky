package cz.doleckovi.piskvorky.gtp.trait;

/** Terminal marker: accepted-and-last at every pipeline stage that interprets it.
 *
 * <p>Each stage stops accepting further work the moment it accepts a carrying item
 * (later submissions get {@link java.util.concurrent.RejectedExecutionException}),
 * still processes that item fully, and its thread then exits; upstream stages observe
 * the refusal and wind down the same way. Produced by {@code quit} (TODO.md [21]),
 * which makes it the end-of-session signal of GTP spec &sect;6.3.1: reading stops,
 * queued work still runs and gets dispatched, the loop finishes the quit sequence
 * and the session ends.</p>
 *
 * <p>Acceptance of the marker is the terminal event - the submission itself does
 * <em>not</em> wait for the drain. Whether every accepted command was answered before
 * the conversation is disposed is observed through the stages' termination hooks,
 * which the owning session joins before it releases the transport.</p>
 *
 * <p>{@link Canceling} is the one obligation the hierarchy states: ending a
 * conversation must wind down a {@link Cancelable} command still running for a peer
 * that has gone away - every terminal path ({@code quit}, EOF, broken input, host
 * abandonment) is a {@code Canceling} submission.</p>
 */
public interface Shutdown extends Canceling {
}
