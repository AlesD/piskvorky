# TODO

Task tracker for this repo. Conventions:

<!-- status: open | in-progress | blocked | done -->
<!-- priority: high | medium | low -->

- Each item is an `## [N] Title` heading; `[N]` is a stable id — reference items as
  `[N]` in conversation, commit messages and `Blocked by:` lines. Ids are never
  reused: a gap in the numbering means the item was completed and deleted.
- **Next id: `[33]`.** To add an item, use this id and then bump this line to the next
  unused number (the highest id ever used, plus one — check git history if unsure,
  since completed items are deleted). Never reuse an id, even after its item is gone.
- The first line below the heading carries one-line metadata:
  `status: …    priority: …    created: YYYY-MM-DD` (add `updated:` when it changes).
- Add `Blocked by: [N] (short summary)` in the body when relevant.
- Completed items are **deleted** — git history is the archive.

## [9] Add file GTP transport

status: open    priority: medium    created: 2026-10-09

Beyond `StdioTransport`: implement the file/scenario transport — read setup position
+ requests from files, output to a file or an in-memory buffer, diagnostics to STDERR.
The in-memory sink is what the regression-test harness needs. If file content is ever
fed wholesale to `parser.input()` (grammar validation), remember the trailing-newline
wrapper from the grammar comment — never rely on scenario files ending with `'\n'`.

## [10] Add TCP GTP transport

status: open    priority: low    created: 2026-10-09

Beyond `StdioTransport`: implement the TCP transport (socket stream in/out,
diagnostics to STDERR; single-session per process). `stop()` should `shutdownInput()`
so a blocked read is genuinely woken (see transport `package-info.java`). Use the
transport abstraction, not `Resource` (see AGENTS.md "GTP I/O transports"). For the
future web GUI / separate GTP process.

## [11] Regression-test harness on file transport

status: open    priority: low     created: 2026-10-10

Scenario files (setup position + requests, à la GNU Go regression suites): run
through the file transport with responses captured to an in-memory buffer,
compare against expected output (messages→stderr). Depends on [9] (file transport)
for the transport and on deterministic results from `reg_genmove`
(no move randomization in regression mode). Note: a scenario's "setup position" is
*session creation* with a crafted initial position ([24]), not `clear_board`
(new game, same board) — see AGENTS.md session & lifecycle model.

## [12] GoGui compatibility: analyze commands + live graphics

status: open    priority: medium    created: 2026-10-10

GoGui is the intended front end before `piskvorky-web` exists (see docs/gogui/).
Implement `gogui-analyze_commands` (private-extension naming per spec §2.13)
reporting supported analyze types, emit `gogui-gfx:` live-graphics lines via
`messages()` while commands (searches) run, and consider `gogui-gfx: CLEAR` before
response end to avoid the documented stderr/response race.

## [13] Playable against GoGui over STDIO (M1 smoke test)

status: open    priority: medium    created: 2026-10-10

End-to-end check once transport wiring lands: run the app over STDIO, play a full
game against GoGui (boardsize, clear_board, play, genmove, live graphics visible
during search, quit ends cleanly). Validates the "develop without building a GUI"
premise of AGENTS.md.
Blocked by: — ([7] landed 2026-10-11, STDIO smoke passes for the admin set; remaining
work is the game/engine commands themselves — [24], [12], [20])

## [14] Commands must not return results ending with '\n'

status: done    priority: low    created: 2026-10-10

Follow-up from the Action framing decision: `Result.toWire()` inserts answer
text verbatim, so a command result ending in its own newline produces an extra blank
line (harmless per spec §2.10 but sloppy). When visiting the legacy `command/`
package (the command cleanup with [24]), normalize results to carry no trailing
newline. `StringJoiner("\n")`-style results (`ListCommandsCommand`) are already
fine; audit the rest (esp. multi-line ones like `RulesBoardCommand`, which currently
even starts with `'\n'`).

## [15] GTP lexer review

status: open    priority: low    created: 2026-10-10

Per-line parsing is proven ([done] — see git history); revisit the lexer while
touching grammar anyway:
- case-insensitive command names per spec §3.3 (`ID` currently case-sensitive while
  `VERTEX`/`WHITE`/… use `caseInsensitive=true`);
- `INTERRUPT` vs `IGNORED` comment collision is match-order dependent: plain
  `# interrupt` only works because of the same-length tie resolved by declaration
  order, and `# interrupt extra` silently degrades to a comment/noop (pinned by
  `AntlrCommandParserTest`) — make it intentional (e.g. `INTERRUPT: '# interrupt' WS
  (EOL|{EOF}?)`-style or a dedicated mode);
- `-> mode(DEFAULT_MODE)` transitions are dead weight with fresh per-line lexers
  (harmless; only relevant if a whole-stream lexer path ever returns);
- `Vertex.parse`'s skipped-`I` handling is unreachable through the grammar (the
  `VERTEX` regex excludes `i`) — pick one owner of the rule.

## [17] Check Spring Boot 3.5.7 version conflicts

status: open    priority: low    created: 2026-10-09

Toolchain note in AGENTS.md: parent POM uses Spring Boot 3.5.7 — verify there are no
dependency/version conflicts across modules (JUnit Jupiter 6.0.0, Mockito, AssertJ).

## [24] Grow api Game into the full match container

status: open    priority: medium    created: 2026-10-11

`Game` is the protocol-agnostic match container (api), not a new type. Pillars, added when
commands are revisited (command-per-command, after [7]):
- Board reference — shared, one instance per size via app-level board factory/pool; game's
  board size must match positions' board. Positions may exist without games (engine caches).
- Initial position — arbitrary position (uneven stone counts, BLOCK stones for problem
  layouts / board holes), not necessarily empty. `clear_board` = new game with standard empty
  initial on same board; loading a crafted initial is session creation, not a GTP command.
- Move list — moves from initial to current, rule-checked (sides alternate, no BLOCK/EMPTY
  plays, field must be empty, no play after terminal — enforcement owned by core `GameImpl`;
  commands query it via api instead of hardcoding legality as `RulesLegalMovesCommand`
  does today). Design as iteration over a tree-ready persistent sequence, NOT
  index-truncate `List` — future variants (positions as persistent nodes), clock
  information and comments must not force a breaking change.
- Metadata — players (own entities: name, id, rating, engine settings), time/place,
  tournament info, final outcome. Backs app's game-library inspection.
- No serialization of core instance classes — external representations only.
- Link to GTP side: `parser`/`action` `Vertex`+`Move` are the *wire format* of an api move;
  turning wire into game move requires the board — happens in command machinery, revisit
  when commands are touched. Do not merge the two `Move` types.

## [25] Split parser port from ANTLR adapter (parser.antlr)

status: open    priority: low    created: 2026-10-11

Confine ANTLR to one subpackage so a hand-written alternative parser is a drop-in:
- `parser` keeps only `CommandParser` (port) + `package-info`;
- move `AntlrCommandParser`, `CollectingErrorListener` to `parser.antlr`;
- relocate `.g4` files to `src/main/antlr4/cz/doleckovi/piskvorky/gtp/parser/antlr/` —
  generated classes follow the folder-derived package (build-helper adds the generated root
  already; drop the stale commented `outputDirectory` config);
- delete dead `parser.Color` enum (nothing uses it; `AntlrCommandParser` maps grammar contexts
  straight to `api.game.Side`);
- `Vertex.parse(String)` ownership reviewed with [15] (grammar excludes `i` but parse
  re-implements skipped-I — pick one owner); the *value types* stay in `action`
  (`CommandFactory` argument vocabulary), parsing behavior moves parser-side.

## [26] GTP extension: load/save game of the session

status: open    priority: low    created: 2026-10-11

Depends on: [24] (external representations of initial position + move list + metadata)

Private-extension commands (GoGui naming per spec §2.13) to load a game (initial position +
move list + metadata) into / save it out of the live session — the GTP-side hook for the
library described in AGENTS.md. Distinct from `clear_board` (new game, same board). Needs
[24]'s external representations first.

## [31] Clean output

status: open    priority: low    created: 2026-10-16

Test errorSettlesTaskAndCancelsQueuedWork shows exception escaping run() method logged by
uncaught exception handler. We should not let uncaught exception escale if possible and
if not possible then use own handler that would log them. Otherwise the exception will
polute STDERR which is used for messages.

ShutdownAction is visible in output. How does it happen?

## [32] Reconcile docs with the reworked execution pipeline

status: open    priority: medium    created: 2026-10-19

The `execution` rewrite (`ExecutionLoop` = submit-only surface, termination hooks,
`Shutdown` no longer `Sync`, `Cancelable.cancel()` split into `cancelDuringExecution()`
/ `cancelBeforeExecution()`, `SubmitRejectedException`) left the prose behind. Statements
now contradicted by the code:

- `ActionProcessingLoop` class javadoc: links `#handleException`, `#windowOpened`,
  `#windowClosed` (no such methods) and `#runningTask` in `accepted`'s javadoc (the field
  is `currentCancellable`); "the stage never interrupts its worker" (it does, when
  `cancelDuringExecution()` answers `true`); "Following submissions return
  RejectedExecutionException" and the Failures section's "REE rethrown unchanged"
  (only `SubmitRejectedException` marks a dead sink — a body's REE is an ordinary
  failure); "a refused submission settles its own future exceptionally" (a refused
  submission creates no future at all).
- `Cancelable` class javadoc: `{@link #cancel()}` no longer exists and the "fully
  cooperative, the stage never interrupts its worker" story is the old protocol.
- `execution/package-info.java`: the `Canceling` bullet ("never interrupts its worker,
  `cancel()` only flips provider-owned state") and the `Shutdown` bullet naming
  `RejectedExecutionException`; add the head's local refusal (`submit` refuses after it
  forwards a `Shutdown`) and name `SubmitRejectedException` as the stage's refusal type.
- `trait/Shutdown.java` javadoc: "(later submissions get
  `java.util.concurrent.RejectedExecutionException`)".
- `AGENTS.md`: the `execution` package description + the session/lifecycle bullet should
  name `SubmitRejectedException` and the head's refusal.
- Method javadoc typos to catch while in there: `executinon`, "to give give it chance",
  "Cancelling action addedto queue", "Polite request abandon".
