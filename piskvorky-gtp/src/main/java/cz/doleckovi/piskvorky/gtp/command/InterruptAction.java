package cz.doleckovi.piskvorky.gtp.command;

public class InterruptAction extends NoOpAction implements InterruptingAction {

	public static final InterruptAction INSTANCE = new InterruptAction();

	private InterruptAction() {
		super();
	}

	@Override
	public String toString() {
		return "InterruptAction";
	}

}
