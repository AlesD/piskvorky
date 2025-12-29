package cz.doleckovi.piskvorky.gtp.command;

import java.util.concurrent.Callable;

public interface Command extends Callable<String> {

	@Override
	String call() throws CommandException;

}
