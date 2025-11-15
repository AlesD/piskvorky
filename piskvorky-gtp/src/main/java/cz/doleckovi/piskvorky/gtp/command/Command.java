package cz.doleckovi.piskvorky.gtp.command;

public interface Command {
	String execute() throws CommandException;
}
