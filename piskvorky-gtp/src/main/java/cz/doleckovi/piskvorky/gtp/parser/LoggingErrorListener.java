package cz.doleckovi.piskvorky.gtp.parser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingErrorListener extends BaseErrorListener {

	private static final Logger LOG = LoggerFactory.getLogger(LoggingErrorListener.class);

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
		super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
		LOG.error("Syntax error: [{}:{}] {}", line, charPositionInLine, msg);
	}

}
