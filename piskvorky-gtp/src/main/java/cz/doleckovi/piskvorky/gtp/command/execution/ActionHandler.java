package cz.doleckovi.piskvorky.gtp.command.execution;

import cz.doleckovi.piskvorky.gtp.command.Action;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class ActionHandler implements Runnable, InvocationHandler {

	private final Action target;

	public ActionHandler(Action target) {
		this.target = target;
	}

	@Override
	public void run() {
		target.execute();
	}

	@Override
	public Runnable invoke(Object proxy, Method method, Object[] args) throws Throwable {
		var interfaces = target.getClass().getInterfaces();
		interfaces = Arrays.copyOf(interfaces, interfaces.length + 1);
		interfaces[interfaces.length - 1] = Runnable.class;
		return (Runnable) Proxy.newProxyInstance(ActionHandler.class.getClassLoader(), interfaces, this);
	}
}
