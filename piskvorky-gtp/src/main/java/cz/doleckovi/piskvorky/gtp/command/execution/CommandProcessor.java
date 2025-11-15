package cz.doleckovi.piskvorky.gtp.command.execution;

import cz.doleckovi.piskvorky.gtp.command.Command;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class CommandProcessor {

	private static final Logger LOGGER = Logger.getLogger(CommandProcessor.class.getSimpleName());

	private final Lock lock = new ReentrantLock();
	private final CommandExecutor executorService = new CommandExecutor();

	public void executeCommand(Command command) {
		executorService.submit(command);
	}

}
