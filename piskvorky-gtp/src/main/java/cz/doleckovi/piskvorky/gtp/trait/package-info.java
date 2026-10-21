/**
 * Execution traits — pure-intent markers carried by commands and results of the GTP
 * pipeline. The interfaces in this package named by {@link cz.doleckovi.piskvorky.gtp.trait.Trait}
 * ({@link cz.doleckovi.piskvorky.gtp.trait.Canceling},
 * {@link cz.doleckovi.piskvorky.gtp.trait.Sync},
 * {@link cz.doleckovi.piskvorky.gtp.trait.Shutdown}) declare <em>what</em> a task intends
 * without defining <em>how</em> it is honored: the action layer carries them, the
 * execution layer interprets them. Markers declare no methods — an interface with an
 * implementation is behavior of the object declaring it, not a trait:
 * {@link cz.doleckovi.piskvorky.gtp.trait.Cancelable} lives here as the permanent
 * capability neighbor of {@link cz.doleckovi.piskvorky.gtp.trait.Canceling} — its
 * implementation stays with the declarer, the execution layer honors it and it is
 * never copied onto another object. The only code beyond the markers is
 * {@link cz.doleckovi.piskvorky.gtp.trait.Traits} — the machinery letting an object claim
 * another object's marker interfaces (e.g. {@code Shutdown} claimed by the
 * {@link cz.doleckovi.piskvorky.gtp.action.Result} of a {@code quit} command) without
 * transferring any implementation.
 *
 * <p>The markers are not flat: {@link cz.doleckovi.piskvorky.gtp.trait.Shutdown} extends
 * {@link cz.doleckovi.piskvorky.gtp.trait.Canceling}, because a terminal submission
 * must wind down a {@link Cancelable} command still running for a vanished peer. The
 * hierarchy turns that obligation into a type, so a carrier
 * can never forget it; claiming via {@link cz.doleckovi.piskvorky.gtp.trait.Traits}
 * is subtype-aware and never re-adds a supertype a claimed subtype already implies.</p>
 *
 * <p>This package is a leaf of the {@code piskvorky-gtp} module: it depends on the JDK only.
 * Do not add behavior here — interpretation of an intent belongs to the package that acts
 * on it.</p>
 */
package cz.doleckovi.piskvorky.gtp.trait;
