# AGENTS.md — Pískovice Board Game Project

## Project Overview

Pískovice (5-in-row) is a board game implementation monorepo. **GTP is only one way to
access the game** — it lets the app be developed and played without building a GUI, because
third-party programs that speak Go Text Protocol can act as the front end.

| Module | Purpose | Intended dependencies |
|--------|---------|-----------------------|
| `piskvorky-api` | Common "glue" — DTOs and interfaces shared by the other modules | — (pure Java, no Spring) |
| `piskvorky-core` | Core game logic; independent of any external interface | → api (ideally **no** Spring) |
| `piskvorky-gtp` | Everything about the GTP protocol, nothing more | → api only (pure Java, **no** Spring, **no** core) |
| `piskvorky-app` | The application itself — all autoconfiguration, the main entry point, long-running, may host multiple games at once | → core + gtp + spring-boot* |
| `piskvorky-web` | Web UI, talks to `piskvorky-api` over REST (client may be non-Java) | *(not present yet)* |

\* autoconfigure, test-autoconfigure

> `piskvorky-gtp2` is **dead code and slated for removal**. Do not add new functionality there.

`piskvorky-gtp` may end up as a **separate process** that talks to `piskvorky-app` through an
internal protocol (e.g. REST). Within that scope, GTP's TCP transport is treated as
**single-session per process** — multi-game / multi-session concerns belong to `piskvorky-app`,
not to the GTP module.

### Intended `piskvorky-app` capabilities
- Inspect games loaded from libraries (files first; possibly a DB or online later).
- Play games between humans (or remote engines) over the network — two web sessions.
- Play games against a local engine — over the web, or eventually a thick client (GTK+).

### GTP I/O transports (design target)
- **STDIO** (primary): read STDIN, write STDOUT/STDERR — for interactive GTP front ends.
- **TCP**: read/write the socket stream (for a future web GUI); diagnostics still go to STDERR.
- **Files**: read pre-defined game *scenarios* (setup position, request move) for testing;
  output goes to a file or an in-memory buffer, diagnostics still to STDERR.

A transport is a **paired input + output** plus a process-level STDERR. `Resource` is suitable
only for *file input*; it must not be overloaded to represent TCP (use a transport abstraction).

**Java:** 25 (`openjdk-21` on Ubuntu won't work as required version is enforced).

## Task Tracking

Work items live in the top-level `TODO.md` — read it when starting work and keep it current:
claim items (`status:`), note blockers, and **delete completed items when committing the fix**
(git history is the archive). Format: one `## ` heading per item, first line below it is
`status: open | in-progress | blocked | done    priority: high | medium | low    created: YYYY-MM-DD`.

## Entry Points & Executables

### Running services
```bash
# Main app (Spring Boot) — hosts games. The `gtp` profile (active by default through
# application.properties) wires the STDIO GTP session: the process speaks GTP on
# stdin/stdout/stderr (banner off, logs redirected to stderr by logback-spring.xml —
# stdout stays pure protocol). There is no standalone piskvorky-gtp executable jar —
# piskvorky-gtp is a library module; assembly lives in piskvorky-app only.
mvn spring-boot:run \
    -pl piskvorky-app \
    -Dspring-boot.run.profiles=local,gtp
```

### Building & Deploying
```bash
# Install monorepo (updates all modules)
mvn clean install

# Build single module (requires parent install first)
mvn compile -pl piskvorky-core
```

## CI / Development Workflow

**Required sequence:** lint/compile → typecheck (maven-compiler-plugin enforces Java 25).  
**Tests use JUnit Jupiter 6.0.0 + Mockito + AssertJ.**

| Goal | Command |
|------|---------|
| Full build + tests | `mvn clean verify` |
| Run test for one module | `mvn test -pl <module>` |
| Quick compile + lint | `mvn compile package` |
| Structural (AST-aware) search | `ast-grep -p '<pattern>' -l java <path>` |
| Generate docs/site | `mvn site` |

### AST-aware search (`ast-grep`)

Installed as a standalone binary in `~/.local/bin` (not Debian-packaged; upgrade by
downloading the release zip from GitHub). Prefer it over text `grep` for structural
queries. Patterns look like code, not regex: `$VAR` matches one AST node, `$$$`
matches any number (`ast-grep -p 'class $NAME { $$$ }' -l java piskvorky-gtp`).
Run `ast-grep --help` or see https://ast-grep.github.io for rule files and rewriting.

## Code Structure

- **Domain**: `/piskvorky-core/src/main/java/cz/doleckovi/piskvorky/core`
- **Business logic/API contracts**: `/piskvorky-api/src/main/java/cz/doleckovi/piskvorky/api`
- **Game protocols**: GTP in `piskvorky-gtp` (`cz.doleckovi.piskvorky.gtp`); `piskvorky-gtp2` is dead.
  Package layering inside `piskvorky-gtp` (acyclic): `transport` and `trait` are JDK-only leaves
  (`trait` = pure-intent marker interfaces + the `Traits` copy helper, no behavior;
  `Cancelable` lives there too as the permanent capability neighbor of `Canceling` — it
  carries an implementation, is NOT a trait and is never copied), `action` → trait+transport
  (unit of work `Action`, `Context`
  = session facet (messages channel + board/game), framing in `Result`), `parser` → action+command
  (command lines in, Actions out; `CommandFactory`/`Command` and the `Vertex`/`Move` value types are
  its seam), `execution` →
  action+trait+parser+transport (the pipeline over the `ExecutionLoop` surface: the
  queue-and-worker stage `ActionProcessingLoop`, the self-contained dispatch stage `ResultWritingLoop` and the
  pass-through reading head `CommandReadingLoop` (`ExecutionLoop<Action, Result>`, its
  pump parses each wire line on its own thread and forwards actions on the submitting
  thread; `Sync` and `Canceling` are interpreted by the action stage only) + the
  `ShutdownAction` terminal marker
  (the head's own on EOF and broken input, a host's via `Session.close()`; terminal
  submissions never block - the session joins the stage termination hooks); every stage
  takes a termination hook invoked on its exiting worker), `command` →
  action+trait+api (the `Command`/`CommandFactory`/`CommandException` contracts and the command bodies
  implementing `Command`, reading session state from their
  execution `Context`, plus the `CommandAction` adapter wrapping a command as an `Action`
  (`CommandAction.of` stamps the command's markers onto action and result, picking the
  `CancelableCommandAction` variant for `Cancelable` commands); the stateless `GtpCommandFactory` covers the standard GTP set,
  subpackages `command.gogui` (extensions, e.g. interrupt) and `command.gogui.rules`
  (`gogui-rules_*`) hold their own factories, composed via `CompositeCommandFactory`),
  `session` → everything below it
  (**top level**: `Session` owns transport + game + the three stages and implements `Context`;
  its lifecycle thread drives end-of-life cleanup through the host callback). Keep new edges
  pointing this way; cycles between packages are not acceptable.
  Planned additions keep the graph acyclic and top-heavy: `parser.antlr` (adapter:
  `AntlrCommandParser` (tree building merged in), `CollectingErrorListener` + generated ANTLR classes;
  `parser` itself stays the implementation-free port, TODO [25]).
- **GTP transport**: `cz.doleckovi.piskvorky.gtp.transport` — pure `java.io` (no Spring,
  no ANTLR, no core). Line-oriented surface: `GtpTransport` exposes `commands()`
  (`CommandReader.readLine()`) + `responses()` / `messages()` (`LineWriter.writeln()` —
  atomic block of one or more lines, always terminated by exactly one `\n`), with
  `stop()/stopped()/close()`. All three destinations are required when building a
  `StreamTransport` (there is no "merged stream" mode — combining streams is the
  client's business, shell `2>&1`; handing the same `Writer` to both output channels is
  legal and shares one destination lock).
  `StreamTransport` + `StreamTransportBuilder` (the only place raw `Reader`/`Writer`
  appear) are the generic building blocks; `StdioTransport` is the primary factory
  (stdin / stdout / stderr, messages→stderr by default — what GoGui live graphics
  consume). The architecture (channel semantics, line/block discipline, ordering, stop
  semantics, charset policy: per-stream `stdin/stdout/stderr.encoding` defaults with
  UTF-8 fallback, `-Dpiskvorky.gtp.encoding` override) is documented in that package's
  `package-info.java`. Command lines are consumed via `gtp.parser.CommandParser`
  (per-line ANTLR parsing into `gtp.action.Action`s) by the `execution` pipeline stages,
  wired and owned by the top-level `gtp.session.Session` (TODO.md [29]); the TCP/file
  transports are pending ([9], [10]).
- **Session & lifecycle model (agreed 2026-10-11, wired by [29])**: the match container is `api.game.Game`
  (evolving, see TODO [24]) — no separate "PlaySession" type. Ownership chain is
  `Game → initial Position → Board`; positions are free-floating immutable values that may
  exist without any game (engine caches key on them across games; consecutive positions of a
  game share structure by construction). Boards are shared: ideally one `Board` instance per
  size, minted by an app-level board factory/pool; a game's board size must match the board
  referenced by its positions. Core instance classes are never serialized — everything has an
  external representation. `clear_board` = *new game* on the same board (fresh `Game` from the
  pool, standard empty initial position), NOT "reload a stored initial"; loading a saved /
  scenario game is **session creation**, not a GTP command (own load/save GTP extension is a
  later option, TODO [26]). The engine attaches at assembly as `api.search.Engine` and is never
  a GTP-internal type; engine narration (analysis/live-graphics of search) rides `Context.messages()`
  — one channel, two sources. **`gtp.session.Session` is the top-level protocol conversation
  object**: it owns the transport, the current board+game, the three execution stages and a
  lifecycle thread, and
  *is* the `Context` commands execute against (game state is worker-confined). Lifecycle:
  construction starts the stages and the lifecycle thread; quit/EOF/host `close()` end them;
  the lifecycle thread then stops+closes the transport and hands the ended session to the
  `onTermination` callback given at assembly — end-of-life cleanup (for the STDIO MVP:
  application exit; later game rescue, [24]) lives in that callback, which the host may fan
  out itself. Spring assembly belongs to `piskvorky-app` only.
- **Normative references**: `/docs` — GTP v2 spec (draft 2) and GoGui manual chapters
  with design rationale; see `README.md`.

### Coding conventions (preferences)

- **One class per file.** Nested classes only for tiny data carriers (e.g. records).
  Helpers like `StoppableCommandReader` / `LockingLineWriter` (one adapter per channel:
  framing only — resource ownership is settled by `StreamTransportBuilder`) are
  package-visible top-level classes so they can be unit-tested directly.
- **Member order within a class:** static nested classes, static fields, static methods,
  instance inner classes, instance fields, instance constructor, instance methods.
- Keep classes focused on one piece of functionality, reasonably sized and documented.
- `package-info.java` documents what the package offers to its *users* today: its
  contract and package-wide facts (threading expectations, shared data types). It is
  not a store of design history or of reasons why the current implementation is the
  way it is &mdash; implementation mechanics belong in that implementation's
  class javadoc (e.g. ANTLR mechanics in `AntlrCommandParser`, not in the `parser`
  package-info, which describes the parser-agnostic `CommandParser` contract).
  Alternatives considered go to git history, not to docs.
- **Javadoc states only what the annotated element itself does.** Do not restate a
  collaborator's or a subclass's behavior on it, even attributed ("traits copied by X",
  "cancellation delegated to Y") &mdash; document that on X/Y; a class must not claim work
  it does not perform. Describe the element's own contract (for a factory, the contract of
  what it returns) and its own mechanics &mdash; not design history, alternatives, or
  cross-cutting rationale ("so this package stays free of &hellip;"): git is the archive.
- **Use imports, not fully-qualified names, in javadoc.** The acyclic package layering is
  about runtime/compile dependencies; `import` is lexical and creates no bytecode edge, so
  a javadoc-only import that crosses the graph is fine. Reserve FQNs for what cannot be
  imported (e.g. a `{@link package}` link).

## Current State vs. Target Architecture (known deviations)

The target isolation of [1] is now enforced: `piskvorky-gtp` compiles against `piskvorky-api`
only (`piskvorky-core` remains a *test-scope* dependency for wiring real game objects in
tests); `piskvorky-app` owns all Spring autoconfiguration and wires core implementations at
assembly (`GtpConfiguration`). The session rewire of [29] landed: the top-level
`gtp.session.Session` owns transport + game + the three `execution` stages
(`CommandReadingLoop` → `ActionProcessingLoop` → `ResultWritingLoop`) and a lifecycle
thread, terminalization is
by submitting `Shutdown`-carrying tasks only (no stage-level `shutdown()` control API;
each stage's termination hook is observation), and the app ends via the session's
`onTermination` callback. The legacy `gtp.loop` package, `CommandExecutor(I)` and
`ResultProcessor` pair are retired. Known remaining gaps toward today's target:

- **MVP command caveat (from [7], shrinking via [24])**: administrative, setup (`boardsize`,
  `clear_board`) and GoGui rules commands work end-to-end through the session `Context`;
  `play` executes against the core game but the rest of the game-command set
  (`genmove`/search, `undo`, setup moves) still awaits its command-by-command revisit [24].
- **Game disposal at conversation end**: `quit` no longer closes the game itself — the session
  just ends the conversation and its end-of-life callback carries cleanup; rescuing the
  finished game before disposal is host work inside that callback ([24]).
- **Diagnostics**: `Context.messages()` is the transport's messages channel directly (the old
  `MessageContext` PrintWriter bridge is gone); a GoGui live-graphics rebuild ([12]) writes
  whole blocks through it.

## Toolchain Quirks

- **Maven:** system `mvn` is the runner — the broken maven wrapper
  (`mvnw`/`mvnw.cmd`, missing `.mvn/wrapper/`) has been removed from the project.
- Parent POM defines Java 25 enforcer rules.
- Spring Boot 3.5.7 as parent → need to check for version conflicts.
- Mockito injection in surefire plugin: `-Dspring-boot.run.profiles=local,gtp`

## Conventional Commands

```bash
# Debug core module (no API dependencies needed)
mvn compile -pl piskvorky-core -am

# Check GTP protocol implementations
find . -name "*Gtp*.java" -type f
```
