# Chapter 10. Interrupting Commands

Source: https://www.kayufu.com/gogui/interrupt.html — *GoGui Documentation*, Markus Enzenberger, revision 1.6.0, © 2014. Retrieved 2026-10-10 (reference copy).

---

Commands can be interrupted, if the Go program supports it. This is useful, for example, if the move generation takes a long time, to give the user the ability to stop the current move generation and gain control over the interface again.

There are two ways of sending the interrupt signal to the program:

1. The Go program reads the GTP input stream, while processing a command, and can interrupt the current command, if the special comment line **`# interrupt`** is received. The Go program indicates this ability by implementing the command **`gogui-interrupt`**.

2. The alternative way is that the program can handle INT signals. It indicates this by implementing the **`gogui-sigint`** command and returning its process ID. GoGui will then send an INT signal to the process by invoking the shell command **`kill -INT pid`**. However, this will work only on UNIX systems and lacks the feature of transmitting interrupt signals over network GTP streams.

If the program does not support any of these methods, GoGui will offer to kill and detach the program.
