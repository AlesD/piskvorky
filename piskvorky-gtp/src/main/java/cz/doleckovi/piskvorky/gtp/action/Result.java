package cz.doleckovi.piskvorky.gtp.action;

import java.util.StringJoiner;

import cz.doleckovi.piskvorky.gtp.transport.GtpTransport;

/** Outcome of an Action.
 * <p>Results carry traits of actions that produced them.</p>
 */
public interface Result {

	/** Result for actions producing no output at all (ignored lines, interrupt). */
	Result IGNORED = new Ignored();

	/** Command ID to echo.
	 * @return ID value or {@code null}
	 */
	String id();

	/** Formats the result for output.
	 * <p>Output consists of {@code "=" | "?"} marker, optional command ID
	 * ({@code " " + id}) and optional answer ({@code " " + text}). Only
	 * the first line carries the marker and ID; embedded {@code '\n'}
	 * separates answer lines). Output is always terminated by {@code '\n'}.</p>
	 * @return Fully formatted result or {@code null}
	 */
	String format();

	private static String doFormat(String prefix, String id, String text) {
		var joiner = new StringJoiner(" ").add(prefix);
		if (id != null && !id.isEmpty())
			joiner.add(id);
		if (text != null && !text.isEmpty())
			joiner.add(text);
		return joiner + "\n";
	}

	/** Successful outcome carrying the answer text (spec &sect;3.4). */
	record Success(String id, String answer) implements Result {

		@Override
		public String format() {
			return doFormat("=", id, answer);
		}

	}

	/** Failed outcome carrying the error message (spec &sect;3.5). */
	record Failure(String id, String message) implements Result {

		@Override
		public String format() {
			return doFormat("?", id, message);
		}

	}

	/** Result never written to the output. */
	record Ignored() implements Result {

		@Override
		public String id() {
			return null;
		}

		@Override
		public String format() {
			return null;
		}

	}

}
