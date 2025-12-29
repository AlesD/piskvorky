package cz.doleckovi.piskvorky.gtp.command.trait;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public abstract class Traits {

	private static final Logger LOG = LoggerFactory.getLogger(Traits.class);

	/** Creates {@link Proxy} that will add {@link WithId} interface.
	 * @param target Object that will implement the {@code WithId} interface
	 * @param id Value that will be returned from {@link WithId#id()}
	 * @return Proxy implementing all interfaces of target and the {@code WithId}
     * @throws NullPointerException if any parameter is {@code null}
     * @throws IllegalArgumentException if the target already implements {@code WithId} interface
	 */
	public static WithId addId(Object target, String id) throws NullPointerException, IllegalArgumentException {
		LOG.trace("Adding ID {} to {}", id, target);
		requireNonNull(target, "Parameter 'target' is null");
		requireNonNull(id, "Parameter 'id' is null");
		if (target instanceof WithId)
			throw new IllegalArgumentException("Parameter 'target' already implements WithId");
        return addInterface(() -> id, target, WithId.class);
	}

    /** Creates {@link Proxy} that will add to target any source trait that it yet does not implement.
     * @param source Object that will provide traits
     * @param target Object that will receive traits
     * @return {@code target} if source does not provide any additional traits or {@link Proxy} implementing all
     *         interfaces of target and any <em>additional</em> traits of source
     * @throws NullPointerException if any parameter is {@code null}
     */
    public static Object copyTraits(Object source, Object target) throws NullPointerException {
        LOG.trace("Adding traits of {} to {}", source, target);
        var addedTraits = new LinkedList<Class<?>>();
        for (var sourceInterface : source.getClass().getInterfaces()) {
            if (!Trait.class.isAssignableFrom(sourceInterface))
                continue; // Not a trait
            if (sourceInterface.isInstance(target))
                continue; // Already provided by target
            addedTraits.add(sourceInterface);
        }
        if (addedTraits.isEmpty())
            return target;
        return addInterfaces(source, target, addedTraits.toArray(new Class<?>[addedTraits.size()]));
    }

    public static <T> T addInterface(T source, Object target, Class<T> type) {
        return type.cast(addInterfaces(source, target, type));
    }

    public static Object addInterfaces(Object source, Object target, Class<?>... interfaces) {
        var targetInterfaces = target.getClass().getInterfaces();
        var sourceInterfaces = source.getClass().getInterfaces();
        var proxyInterfaces = Arrays.copyOf(targetInterfaces, targetInterfaces.length + interfaces.length);
        var offset = targetInterfaces.length;
        outer: for (var addedInterface : interfaces) {
            for (int index = 0; index < offset; index++)
                if (proxyInterfaces[index] == addedInterface)
                    continue outer;
            if (Arrays.stream(sourceInterfaces).filter(addedInterface::isAssignableFrom).findAny().isEmpty())
                throw new IllegalArgumentException("Added interface %s is not implemented by source %s".formatted(addedInterface, source));
            proxyInterfaces[offset++] = addedInterface;
        }
        proxyInterfaces = Arrays.copyOf(proxyInterfaces, offset);
        var proxy = Proxy.newProxyInstance(Traits.class.getClassLoader(), proxyInterfaces, new TraitsHandler(source, target, List.of(interfaces)));
        LOG.debug("Following interfaces of {} will be provided by {}: {}", target, source, interfaces);
        return proxy;
    }

    public static <T> T getOrigin(T instance, Class<T> type) {
        while (Proxy.isProxyClass(instance.getClass())) {
            var handler = Proxy.getInvocationHandler(instance);
            if (handler instanceof TraitsHandler traitsHandler) {
                return traitsHandler.getOrigin(type);
            }
        }
        return instance;
    }

    private Traits() {}

    private static class TraitsHandler implements InvocationHandler {

        private final Object source;
        private final Object target;
        private final List<Class<?>> traits;

        public TraitsHandler(Object source, Object target, List<Class<?>> traits) {
            this.source = source;
            this.target = target;
            this.traits = traits;
        }

        private TraitsHandler(String id, Object target) {
            this.source = (WithId) () -> id;
            this.target = target;
            traits = List.of(WithId.class);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (traits.contains(method.getDeclaringClass()))
                return method.invoke(source, args);
            return method.invoke(target, args);
        }

        private <T> T getOrigin(Class<T> type) {
            if (traits.contains(type))
                return Traits.getOrigin(type.cast(source), type);
            return Traits.getOrigin(type.cast(target), type);
        }

    }

}
