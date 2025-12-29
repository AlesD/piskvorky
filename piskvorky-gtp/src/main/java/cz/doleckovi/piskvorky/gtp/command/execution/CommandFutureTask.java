package cz.doleckovi.piskvorky.gtp.command.execution;

import cz.doleckovi.piskvorky.gtp.command.Command;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

class CommandFutureTask extends FutureTask<String> {

    private Command command;

	public CommandFutureTask(Command command) {
		this.command = command;
		super(command);
	}

}
