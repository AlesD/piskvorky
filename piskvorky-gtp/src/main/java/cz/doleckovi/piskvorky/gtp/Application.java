package cz.doleckovi.piskvorky.gtp;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.concurrent.Callable;

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

	int lineNumber = 0;

	public Application(Scanner stdin, PrintWriter stdout, PrintWriter stderr, boolean debug) {
		this.stdin = stdin;
		this.stdout = stdout;
		this.stderr = stderr;
		parser = new GTPParser(null);
		parser.setBuildParseTree(false);
		if (!debug)
			parser.removeErrorListeners();
	}

	private Command parseLine() {
		var line = stdin.nextLine();
		var lexer = new GTPLexer(CharStreams.fromString(line));
		lexer.setLine(++lineNumber);
		lexer.setCharPositionInLine(0);
		parser.setInputStream(new CommonTokenStream(lexer));
		try {
			var context = parser.action();
			return context.value;
		} catch (RuntimeException e) {

		}
	}

	@Override
	public void run() {
		var parser = new GTPParser(null);
		parser.setBuildParseTree(false);
		var lexer = new GTPLexer(CharStreams.fromString(input));
		lexer.setLine(1);
		lexer.setCharPositionInLine(0);
		parser.setInputStream(new CommonTokenStream(lexer));
		var result = parser.action();
		if (result.exception != null)
			throw result.exception;
	}
}
