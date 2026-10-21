package cz.doleckovi.piskvorky.gtp.parser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;
import java.util.List;

/**
 * ANTLR error listener collecting syntax errors instead of reporting them, so the
 * caller can turn the first one into a protocol error response.
 */
class CollectingErrorListener extends BaseErrorListener {

	private final List<String> errors = new ArrayList<>();

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
		errors.add("%s at %d:%d".formatted(msg, line, charPositionInLine));
	}

	boolean hasErrors() {
		return !errors.isEmpty();
	}

	String firstError() {
		return errors.getFirst();
	}

}
