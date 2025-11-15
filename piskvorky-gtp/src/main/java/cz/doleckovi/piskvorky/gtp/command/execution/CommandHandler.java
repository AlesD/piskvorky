package cz.doleckovi.piskvorky.gtp.command.execution;

import cz.doleckovi.piskvorky.gtp.command.Action;
import cz.doleckovi.piskvorky.gtp.command.Command;
import cz.doleckovi.piskvorky.gtp.command.WithId;
import cz.doleckovi.piskvorky.gtp.parser.IdTrait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.concurrent.Callable;

import static java.util.Objects.requireNonNull;

public class CommandHandler implements Callable<String>, InvocationHandler {

	private static final Logger LOG = LoggerFactory.getLogger(CommandHandler.class);

	private final Command target;

	public static Callable<?> asCallable(Command command) {
		requireNonNull(command, "command is null");
		LOG.trace("Adding Callable<String> interface to {}", command);
		if (command instanceof Callable<?> callable)
			return callable;
		var interfaces = command.getClass().getInterfaces();
		interfaces = Arrays.copyOf(interfaces, interfaces.length + 1);
		interfaces[interfaces.length - 1] = Callable.class;
		var proxy = (WithId) Proxy.newProxyInstance(WithId.class.getClassLoader(), interfaces, new IdTrait(id, target));
		LOG.debug("Added ID {} to {}", id, target);
		return proxy;
	}

	private CommandHandler(Command target) {
		this.target = target;
	}

	@Override
	public String call() {
		return target.execute();
	}

	@Override
	public Runnable invoke(Object proxy, Method method, Object[] args) throws Throwable {
	}
}
