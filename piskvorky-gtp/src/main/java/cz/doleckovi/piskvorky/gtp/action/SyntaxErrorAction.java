package cz.doleckovi.piskvorky.gtp.action;

import cz.doleckovi.piskvorky.gtp.action.Result.Failure;

import java.util.StringJoiner;

/** Action for an unparsable command lines. */
public class SyntaxErrorAction implements Action {

	private final String id;
	private final String message;

	/** Creates new instance.
	 * @param id      ID echoed in the response, or {@code null} for none
	 * @param message error text reported to the controller
	 */
	public SyntaxErrorAction(String id, String message) {
		this.id = id;
		this.message = message;
	}

	/** Request ID echoed in the response, may be {@code null}. */
	@Override
	public String id() {
		return id;
	}

	/** Error text reported to the controller. */
	public String message() {
		return message;
	}

	/** Creates {@link Failure} carrying ID and message
	 * @param context Access to session state
	 * @return Failure response
	 */
	@Override
	public Failure execute(Context context) {
		return new Failure(id, message);
	}

	@Override
	public String toString() {
		var result = new StringJoiner(", ", SyntaxErrorAction.class.getSimpleName() + "[", "]");
		if (id != null) result.add("id=" + id);
		return result.add("message=" + message).toString();
	}

}
