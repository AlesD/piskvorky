package cz.doleckovi.piskvorky.gtp.parser;

import cz.doleckovi.piskvorky.gtp.CommandFactory;
import org.antlr.v4.runtime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Supplier;

public class CommandReader implements CommandSupplier {

	private static final Logger LOG = LoggerFactory.getLogger(CommandReader.class);

	private final Deque<Object> stack = new LinkedList<>();
	private final Supplier<String> lineSupplier;
	private final GTPParser parser;

	private int lineNumber;

	public CommandReader(LineSupplier lineSupplier, CommandFactory commandFactory) {
		this((Supplier<String>) lineSupplier::getLine, commandFactory);
	}

	public CommandReader(Supplier<String> lineSupplier, CommandFactory commandFactory) {
		this.lineSupplier = lineSupplier;
		parser = new GTPParser(null);
		parser.setBuildParseTree(false);
		parser.addParseListener(new ParserListener(commandFactory, stack));
		parser.removeErrorListeners();
		parser.addErrorListener(new BaseErrorListener() {
			@Override
			public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
					int charPositionInLine, String msg, RecognitionException e)
			{
				LOG.error("line {}:{} {}", line, charPositionInLine, msg);
			}
		});
	}

	Object parseLine(String line) {
		LOG.trace("Parsing line: {}", line);
		var lexer = new GTPLexer(CharStreams.fromString(line));
		lexer.setLine(++lineNumber);
		lexer.setCharPositionInLine(0);
		parser.setInputStream(new CommonTokenStream(lexer));
		stack.clear();
		parser.action();
		assert stack.size() == 1;
		return stack.pop();
	}

	@Override
	public Object getCommand() {
		return parseLine(lineSupplier.get());
	}

}
