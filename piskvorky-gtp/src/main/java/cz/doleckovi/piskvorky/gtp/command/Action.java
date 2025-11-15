package cz.doleckovi.piskvorky.gtp.command;

public interface Action {
	void execute() throws CommandException;
}
