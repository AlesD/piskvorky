package cz.doleckovi.piskvorky.gtp.trait;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

/** Machinery around {@link Trait} marker interfaces: {@link #copyTraits(Object, Object)}
 * lets one object claim the markers declared by another. Traits carry no methods, so no
 * implementation is ever transferred - the produced proxy delegates every call to its
 * target and differs from it only in the marker interfaces it claims. Interfaces with
 * implementations ({@link Cancelable}) are deliberately not traits and cannot
 * propagate through this helper. */
public abstract class Traits {

	private static final Logger LOG = Logger.getLogger(Traits.class.getName());

	/** Delegates everything to {@code target}; exists only to carry the extra markers. */
	private static class MarkerProxy implements InvocationHandler {

		private final Object target;

		MarkerProxy(Object target) {
			this.target = target;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			try {
				return method.invoke(target, args);
			} catch (InvocationTargetException e) {
				// the proxy must throw what the underlying method threw, not its wrapper
				if (e.getCause() != null) throw e.getCause();
				throw e;
			}
		}

	}

	/** Creates {@link Proxy} over {@code target} additionally implementing every
	 * {@link Trait} of {@code source} that {@code target} does not claim yet.
	 * @param source Object providing the inventory of markers to claim - consulted for
	 *               <em>which</em> interfaces only, across its whole class hierarchy;
	 *               it provides no implementation
	 * @param target Object that will receive the markers; every call on the proxy reaches it
	 * @return {@code target} if source does not provide any additional traits or {@link Proxy}
	 *         implementing all interfaces of target and any <em>additional</em> traits of source
	 * @throws NullPointerException if any parameter is {@code null}
	 */
	public static <T extends R, R> R copyTraits(Object source, T target) throws NullPointerException {
		LOG.log(Level.FINEST, () -> "Adding traits of " + source + " to " + target);
		requireNonNull(source, "Parameter 'source' is null");
		requireNonNull(target, "Parameter 'target' is null");
		var additional = new LinkedList<Class<?>>();
		for (var iface : collectInterfaces(source.getClass())) {
			if (Trait.class.isAssignableFrom(iface) && !iface.isInstance(target)) {
				additional.add(iface);
			}
		}
		if (additional.isEmpty()) return target;
		@SuppressWarnings({"unchecked"})
		Class<? extends Trait>[] addedTraits = additional.toArray(new Class[additional.size()]);
		return addTraits(target, addedTraits);
	}

	/** Creates a {@link Proxy} over {@code target} additionally implementing the given
	 * {@link Trait} markers that {@code target} does not claim yet (already-claimed ones
	 * are skipped). Lets a caller state the markers directly when their inventory is
	 * statically known, without needing a source object to read them off (contrast
	 * {@link #copyTraits(Object, Object)}).
	 *
	 * <p>Claiming is subtype-aware: markers are not traits in isolation but a small
	 * hierarchy ({@link Shutdown} extends {@link Canceling}), and a
	 * subtype subsumes its supertypes. A listed trait is skipped when the target already
	 * claims it <em>or a subtype of it</em> ({@code trait.isInstance(target)} covers
	 * both), and when another listed trait is its subtype; a trait that subsumes an
	 * earlier addition drops that addition. A call such as
	 * {@code addTraits(target, Canceling.class, Shutdown.class)} therefore
	 * claims only {@link Shutdown}.</p>
	 * @param target Object to receive the markers; every call on the proxy reaches it
	 * @param traits marker interfaces to claim on the proxy
	 * @return {@code target} if it already claims every listed trait, otherwise a proxy
	 *         implementing {@code target}'s interfaces plus the <em>additional</em> traits
	 * @throws NullPointerException if {@code target} or any element of {@code traits} is
	 *         {@code null} (a null array likewise, before its elements are read)
	 * @implNote The proxy claims every interface {@code target}'s class implements -
	 *           directly or inherited through any superclass (see
	 *           {@link #collectInterfaces(Class)}) - plus the added traits, but never
	 *           {@code target}'s class itself: its cast to {@code R} is safe only when
	 *           {@code target} is passed typed as an interface the proxy will reproduce
	 *           (e.g. {@code Result}, not {@code Result.Ignored}); passing a
	 *           concrete-class-typed {@code target} infers {@code R} as that class,
	 *           which the proxy does not extend.
	 */
	@SafeVarargs
	public static <T extends R, R> R addTraits(T target, Class<? extends Trait>... traits) {
		var addedTraits = new LinkedList<Class<?>>();
		for (var trait : traits) {
			if (trait.isInstance(target))
				continue; // Already claimed by target (directly or through a subtype)
			if (addedTraits.stream().anyMatch(trait::isAssignableFrom))
				continue; // A subtype already queued subsumes this supertype
			addedTraits.removeIf(added -> added.isAssignableFrom(trait)); // drop supertypes this trait subsumes
			addedTraits.add(trait);
		}
		if (addedTraits.isEmpty())
			return target;

		var targetInterfaces = collectInterfaces(target.getClass()).toArray(new Class<?>[0]);
		var proxyInterfaces = Arrays.copyOf(targetInterfaces, targetInterfaces.length + addedTraits.size());
		System.arraycopy(addedTraits.toArray(new Class<?>[0]), 0, proxyInterfaces, targetInterfaces.length, addedTraits.size());
		@SuppressWarnings("unchecked")
		R proxy = (R) Proxy.newProxyInstance(Traits.class.getClassLoader(), proxyInterfaces, new MarkerProxy(target));
		LOG.log(Level.FINE, () -> "Following traits will be added to " + target + ": " + addedTraits);
		return proxy;
	}

	/** All interfaces {@code type} implements - declared directly or inherited through
	 * any superclass. Proxy targets are frequently subclasses adding a capability on top
	 * of an interface-typed base (e.g. {@code CancelableCommandAction extends
	 * CommandAction}); a proxy must reproduce the base interfaces too or the produced
	 * object fails the cast its caller expects.
	 * @param type class to inspect (typically some {@code obj.getClass()})
	 * @return interfaces without duplicates, direct ones first, then per superclass level
	 */
	private static Collection<Class<?>> collectInterfaces(Class<?> type) {
		var collected = new LinkedHashSet<Class<?>>();
		for (var current = type; current != null; current = current.getSuperclass())
			collected.addAll(Arrays.asList(current.getInterfaces()));
		return collected;
	}

	private Traits() {}

}
