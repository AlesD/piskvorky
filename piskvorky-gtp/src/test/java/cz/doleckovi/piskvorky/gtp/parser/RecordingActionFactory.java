package cz.doleckovi.piskvorky.gtp.parser;

import cz.doleckovi.piskvorky.gtp.action.Action;
import cz.doleckovi.piskvorky.gtp.action.ActionFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ActionFactory} test double recording action creation (kind, echoed id and
 * error message) while delegating to the defaults, so returned actions still frame
 * real responses.
 */
class RecordingActionFactory implements ActionFactory {

	record Call(String kind, String id, String message) {}

	final List<Call> calls = new ArrayList<>();

	@Override
	public Action createInterruptAction() {
		calls.add(new Call("interrupt", null, null));
		return ActionFactory.super.createInterruptAction();
	}

	@Override
	public Action createNoOpAction() {
		calls.add(new Call("noop", null, null));
		return ActionFactory.super.createNoOpAction();
	}

	@Override
	public Action createSyntaxErrorAction(String id, String message) {
		calls.add(new Call("syntax", id, message));
		return ActionFactory.super.createSyntaxErrorAction(id, message);
	}

}
