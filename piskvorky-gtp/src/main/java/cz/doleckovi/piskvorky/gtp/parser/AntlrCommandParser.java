package cz.doleckovi.piskvorky.gtp.parser;

import cz.doleckovi.piskvorky.gtp.CommandFactory;
import cz.doleckovi.piskvorky.gtp.autoconfigure.CommandConsumer;
import cz.doleckovi.piskvorky.gtp.autoconfigure.CommandParser;
import cz.doleckovi.piskvorky.gtp.command.Command;
import org.antlr.v4.runtime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.LinkedList;

public class AntlrCommandParser implements CommandParser {

	private static final Logger LOG = LoggerFactory.getLogger(AntlrCommandParser.class);

	private final Deque<Object> stack = new LinkedList<>();
	private final GTPParser parser;

	private int lineNumber;

	public AntlrCommandParser(CommandFactory commandFactory, CommandConsumer commandConsumer) {
		parser = new GTPParser(null);
		parser.setBuildParseTree(false);
		parser.addParseListener(new ParserListener(commandFactory, commandConsumer));
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

	@Override
	public Command parse(String line) {
		LOG.trace("Parsing line: {}", line);
		var lexer = new GTPLexer(CharStreams.fromString(line));
		lexer.setLine(++lineNumber);
		lexer.setCharPositionInLine(0);
		parser.setInputStream(new CommonTokenStream(lexer));
		stack.clear();
		parser.action();
		assert stack.size() == 1;
		return (Command) stack.pop();
	}

}
