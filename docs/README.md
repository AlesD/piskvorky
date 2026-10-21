# Project Documentation — Reference Materials

External reference documents (not project code, not authoritative for building).

## Files

| File | What | Source | License / © |
|------|------|--------|-------------|
| `docs/gtp/gtp2-spec-draft2.md` | Specification of the Go Text Protocol, version 2, draft 2 (Gunnar Farnebäck, Oct 2002). Markdown conversion of the HTML; internal links still point at the original section anchors. | https://www.lysator.liu.se/~gunnar/gtp/gtp2-spec-draft2/gtp2-spec.html | GNU FDL 1.1 (verbatim and modified copies permitted) |
| `docs/gogui/analyze-commands.md` | GoGui manual, ch. 7 *Analyze Commands* (Markus Enzenberger, rev. 1.6.0). | https://www.kayufu.com/gogui/analyze.html | © 2014 Markus Enzenberger (GoGui is GPL; docs likewise) |
| `docs/gogui/live-graphics.md` | GoGui manual, ch. 8 *Live Graphics*. | https://www.kayufu.com/gogui/live-gfx.html | © 2014 Markus Enzenberger |
| `docs/gogui/interrupting-commands.md` | GoGui manual, ch. 10 *Interrupting Commands*. | https://www.kayufu.com/gogui/interrupt.html | © 2014 Markus Enzenberger |

Retrieved 2026-10-09 (ch. 10: 2026-10-10).

## Why these documents

They are the normative basis for the transport design in
`piskvorky-gtp` → `cz.doleckovi.piskvorky.gtp.transport`:

- **GTP spec §2.1 (Character Set):** messages are *8-bit character sequences*,
  US-ASCII for standardized syntax, characters 128–255 only in comments/private
  extensions, *no preferred character set*. Standardized syntax is byte-identical in
  UTF-8, so the choice only affects non-ASCII payload → STDIO defaults to the JVM's
  per-stream process encodings (`stdin.encoding`/`stdout.encoding`/`stderr.encoding`,
  UTF-8 fallback), overridable via `-Dpiskvorky.gtp.encoding` (e.g. ISO-8859-1 for
  byte-transparent pass-through).
- **§2.4/§2.5:** commands are exactly one line (LF-terminated; CR discarded) → the
  one-atomic-write-per-line contract on both sides.
- **§2.6/§3.4:** responses end with **two consecutive newlines** (multi-line result
  blocks).
- **§2.8 (Timing):** engine must respond in command order, may pause within a
  response → single ordered sender thread; no cross-channel ordering guarantees.
- **§6.3.1 (quit):** the full response must be sent before the connection is closed;
  the session terminates → synchronous (Sync trait) handling, then `close()`.
- **GoGui ch. 8:** the engine writes `gogui-gfx:` live-graphics lines to **stderr**
  while a command runs → this *is* the `messages()` channel; the default mapping
  `messages() → System.err` is what GoGui consumes natively. The documented
  stderr/response ordering race validates keeping ordering out of the transport.
- **GoGui ch. 7:** analyze commands are ordinary GTP commands answered on
  `responses()`; the `gfx` syntax is shared with the live-graphics messages.
- **GoGui ch. 10:** two interrupt channels — the `# interrupt` comment line on the input
  stream (advertised by the `gogui-interrupt` command) and SIGINT via `kill -INT pid`
  (advertised by the `gogui-sigint` command returning the PID). Together they justify the
  `Canceling`/`Cancelable` trait design in `gtp.execution` and are why
  `IntSignalHandler.NAME` is protocol vocabulary, not a dead constant.
