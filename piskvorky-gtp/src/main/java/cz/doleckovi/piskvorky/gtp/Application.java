package cz.doleckovi.piskvorky.gtp;

import cz.doleckovi.piskvorky.core.GameImpl;
import cz.doleckovi.piskvorky.core.board.BoardImpl;
import cz.doleckovi.piskvorky.core.board.PositionImpl;
import cz.doleckovi.piskvorky.gtp.command.ExceptionCommand;
import cz.doleckovi.piskvorky.gtp.command.LiveGraphicsCommand;
import cz.doleckovi.piskvorky.gtp.command.SessionCommand;
import cz.doleckovi.piskvorky.gtp.command.parser.GTPLexer;
import cz.doleckovi.piskvorky.gtp.command.parser.GTPParser;

import cz.doleckovi.piskvorky.gtp.session.SessionImpl;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class Application implements Runnable {

	static void main(String[] args) {
		var debug = false;
		for (String arg : args) {
			switch (arg) {
				case "--debug":
					debug = true;
					break;
			}
		}
		var console = System.console();
		if (console != null) {
			var scanner = new Scanner(console.reader());
			var stdout = console.writer();
			var stderr = new PrintWriter(System.err, true, Charset.forName(System.getProperty("stdout.encoding")));
			try {
				new Application(scanner, stdout, stderr, debug);
			} finally {
				stderr.flush();
			}
		} else {
			var scanner = new Scanner(System.in, System.getProperty("stdin.encoding"));
			var stdout = new PrintWriter(System.out, true, Charset.forName(System.getProperty("stdout.encoding")));
			var stderr = new PrintWriter(System.err, true, Charset.forName(System.getProperty("stdout.encoding")));
			try {
				new Application(scanner, stdout, stderr, debug).run();
			} finally {
				stdout.flush();
				stderr.flush();
			}
		}
	}

	private final Scanner stdin;
	private final PrintWriter stdout;
	private final PrintWriter stderr;
	private final GTPParser parser;
    private final Session session;

	int lineNumber = 0;

	public Application(Scanner stdin, PrintWriter stdout, PrintWriter stderr, boolean debug) {
		this.stdin = stdin;
		this.stdout = stdout;
		this.stderr = stderr;
		parser = new GTPParser(null);
		parser.setBuildParseTree(false);
		if (!debug)
			parser.removeErrorListeners();
        session = new SessionImpl(BoardImpl::new, board -> new GameImpl(new PositionImpl((BoardImpl) board)));
	}

	private Command parseLine() {
        try {
            String line;
            do {
                line = stdin.nextLine();
                ++lineNumber;
            } while (line.isBlank() && session.isAlive());
            var lexer = new GTPLexer(CharStreams.fromString(line));
            lexer.setLine(lineNumber);
            lexer.setCharPositionInLine(0);
            parser.setInputStream(new CommonTokenStream(lexer));
            var context = parser.action();
            return context.value;
        } catch (RuntimeException e) {
            return new ExceptionCommand(e);
        }
	}

	@Override
	public void run() {
        while (session.isAlive()) {
            Command command = parseLine();
            if (command instanceof SessionCommand sessionCommand)
                sessionCommand.setSession(session);
            if (command instanceof LiveGraphicsCommand liveGraphicsCommand)
                liveGraphicsCommand.setStdOut(stdout);
            Executors.newSingleThreadExecutor()
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        command.execute(new CommandContext() {
                            @Override
                            public Session session() {
                                return session;
                            }

                            @Override
                            public PrintWriter stdout() {
                                return stdout;
                            }

                            @Override
                            public PrintWriter stderr() {
                                return stderr;
                            }
                        });
                    } catch (Exception e) {
                        stderr.print('?');
                        if (!e.getMessage().isBlank()) stderr.
                    }
                }
            };
        }
	}
}
