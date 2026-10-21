/**
 * The top level of {@code piskvorky-gtp}: one {@link cz.doleckovi.piskvorky.gtp.session.Session}
 * object per bound GTP conversation, owning its transport, its current board and game,
 * and the whole {@link cz.doleckovi.piskvorky.gtp.execution execution} pipeline. The
 * session <em>is</em> the {@link cz.doleckovi.piskvorky.gtp.action.Context} handed to
 * every action it runs — the facet of the session commands may touch.
 *
 * <p>Lifecycle: construction starts the conversation; {@code quit}, EOF, broken input
 * or host-side {@link cz.doleckovi.piskvorky.gtp.session.Session#close()} end it. Once
 * the pipeline has drained and the transport is closed, the session's lifecycle thread
 * hands the ended session to the {@code onTermination} callback given at assembly —
 * end-of-life cleanup lives there (for the STDIO MVP, that cleanup is ending the
 * application; game rescue is a host hook, TODO.md [24]).</p>
 *
 * <p>This package composes {@code transport}, {@code parser}, {@code action},
 * {@code execution} and {@code trait} (with {@code api} types for board/game);
 * nothing depends on it inside the module — only the application wiring constructs
 * sessions. It is pure Java: Spring assembly lives in {@code piskvorky-app} only.</p>
 */
package cz.doleckovi.piskvorky.gtp.session;
