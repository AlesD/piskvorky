package cz.doleckovi.piskvorky.gtp.command.execution;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

class ActionTask<V> extends FutureTask<V> {

	final Object action;

	public ActionTask(Callable<V> callable) {
		this.action = callable;
		super(callable);
	}

	public ActionTask(Runnable runnable, V result) {
		this.action = runnable;
		super(runnable, result);
	}

}
