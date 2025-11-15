package cz.doleckovi.piskvorky.gtp.parser;

import cz.doleckovi.piskvorky.gtp.command.WithId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import static java.util.Objects.requireNonNull;

class IdTrait implements InvocationHandler, WithId {

	private static final Logger LOG = LoggerFactory.getLogger(IdTrait.class);

	/** Creates {@link Proxy} that will add {@link WithId} interface.
	 * @param target Object that will implement the {@code WithId} interface
	 * @param id Value that will be returned from {@link WithId#id()}
	 * @return Proxy implementing all interfaces of target plus the {@code WithId}
	 */
	public static WithId add(Object target, String id) {
		LOG.trace("Adding ID {} to {}", id, target);
		requireNonNull(target, "target is null");
		requireNonNull(id, "id is null");
		if (target instanceof WithId)
			throw new IllegalArgumentException("target implements WithId");
		var interfaces = target.getClass().getInterfaces();
		interfaces = Arrays.copyOf(interfaces, interfaces.length + 1);
		interfaces[interfaces.length - 1] = WithId.class;
		var proxy = (WithId) Proxy.newProxyInstance(WithId.class.getClassLoader(), interfaces, new IdTrait(id, target));
		LOG.debug("Added ID {} to {}", id, target);
		return proxy;
	}

	private final String id;
	private final Object target;

	private IdTrait(String id, Object target) {
		this.id = id;
		this.target = target;
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getDeclaringClass() == WithId.class)
			return method.invoke(this, args);
		return method.invoke(target, args);
	}

}
