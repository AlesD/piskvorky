# Chapter 8. Live Graphics

Source: https://www.kayufu.com/gogui/live-gfx.html — *GoGui Documentation*, Markus Enzenberger, revision 1.6.0, © 2014. Retrieved 2026-10-09 (reference copy).

---

GoGui can display graphical data on the Go board while a command is in progress.
Live graphics commands are considered temporary information about what the program
is currently thinking. For displaying permanent information, use the `gfx` analyze
command type.

The Go program invokes live graphics commands by writing draw commands to its
**standard error stream**. Every line that starts with **`gogui-gfx:`** is
interpreted by GoGui as a draw command using the same syntax as in the `gfx`
analyze command type (however, the merging of multiple TEXT draw commands to a
multi-line text for display in a separate window is not performed). If no draw
command follows immediately after `gogui-gfx:`, all following lines until the next
empty line will be interpreted as draw commands. See the `dummy-live_gfx` command
in gogui-dummy(1) for an example.

Before each set of draw commands, existing markup on the graphical board will be
automatically cleared, but not the text in the status bar.

> **Note**
>
> Since the parsing of the standard error stream stops when the command response is
> received, and Java does not allow to guarantee the correct order of standard error
> and response stream, there is a small chance that a live graphics command, which
> arrives shortly before the response, is handled during the next GTP command. You
> can avoid this situation by pausing for short amount of time after writing live
> graphics commands or by explicitly writing a **`gogui-gfx: CLEAR`** command to
> standard error before the command ends.

---

## Relevance for this project

- This is the concrete use case for the transport's `messages()` channel: pure
  **text** lines (`gogui-gfx:` draw commands, `gfx`-format syntax), written to
  **stderr** while a command runs — no Unicode, no binary payloads.
- The ordering note confirms the design stance taken in
  `cz.doleckovi.piskvorky.gtp.transport`: interleaving of `messages()` and
  `responses()` delivery is expected and legal; the transport provides **no
  cross-channel ordering guarantee** and the GUI copes (optionally with
  `gogui-gfx: CLEAR` before the response ends — a command-level pattern, not a
  transport concern).
