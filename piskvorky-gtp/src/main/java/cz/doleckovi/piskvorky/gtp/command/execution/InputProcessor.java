package cz.doleckovi.piskvorky.gtp.command.execution;

import cz.doleckovi.piskvorky.gtp.command.Command;
import cz.doleckovi.piskvorky.gtp.command.QuitCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

import java.util.concurrent.CompletionService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

public class InputProcessor implements SmartLifecycle {

	private static final Logger LOG = LoggerFactory.getLogger(InputProcessor.class);

	private final AtomicReference<Thread> currentExecutingThread = new AtomicReference<>();

	private final Supplier<String> inputSupplier;
	private final Function<String, Command> commandParser;
	private final CompletionService<String> completionService;

	public InputProcessor(Supplier<String> inputSupplier, Function<String, Command> commandParser,
			CompletionService<String> completionService)
	{
		this.inputSupplier = inputSupplier;
		this.commandParser = commandParser;
		this.completionService = completionService;
	}

	@Override
	public void start() {
		var thread = currentExecutingThread.get();
		if (thread != null && thread.isAlive()) {
			LOG.debug("Already running on {}", thread);
			return;
		}
		var newThread = Thread.ofPlatform()
				.name("command-interpreter")
				.uncaughtExceptionHandler(this::uncaughtException)
				.unstarted(this::execute);
		if (currentExecutingThread.compareAndSet(thread, newThread)) {
			newThread.start();
			LOG.info("Started on thread {}", newThread);
		}
	}

	@Override
	public void stop() {
		var thread = currentExecutingThread.getAndSet(Thread.currentThread());
		try {
			if (thread != null) {
				while (thread.isAlive()) try {
					LOG.debug("Interrupting thread {}", thread);
					thread.interrupt();
					thread.join();
				} catch (InterruptedException e) {
					LOG.warn("Interrupted while waiting for termination of thread {}", thread);
					Thread.currentThread().interrupt();
					return;
				}
			}
		} finally {
			if (currentExecutingThread.compareAndSet(Thread.currentThread(), null))
				LOG.info("Stopped");
		}
	}

	@Override
	public boolean isRunning() {
		var thread = currentExecutingThread.get();
		return thread != null && thread.isAlive();
	}

	private void execute() {
		LOG.info("Execution started");
		while (!Thread.interrupted() && currentExecutingThread.get() == Thread.currentThread()) {
			var input = inputSupplier.get();
			if (input == null) {
				LOG.info("End of input - interrupting");
				Thread.currentThread().interrupt();
			} else {
				LOG.debug("Got input: {}", input);
				var command = commandParser.apply(input);
				LOG.debug("Got command: {}", command);
				completionService.submit(command);
				LOG.info("Submitted for execution: {}", input);
				if (command instanceof QuitCommand) {
					LOG.info("Detected quit - interrupting");
					Thread.currentThread().interrupt();
				}
			}
		}
		currentExecutingThread.compareAndSet(Thread.currentThread(), null);
		LOG.info("Execution finished");
	}

	private void uncaughtException(Thread thread, Throwable e) {
		currentExecutingThread.compareAndSet(thread, null);
		LOG.error("Execution failed", e);
	}
}
