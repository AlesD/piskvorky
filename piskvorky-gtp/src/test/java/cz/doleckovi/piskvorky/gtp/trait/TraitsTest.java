package cz.doleckovi.piskvorky.gtp.trait;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The helper only ever claims <em>marker</em> interfaces: the proxy delegates every
 * call to its target and nothing is ever routed to (or implemented by) the source.
 * Interfaces with an implementation (like {@link Cancelable}) are not traits and
 * must not propagate through it.
 */
class TraitsTest {

    /** Marker trait claimed by the test sources. */
    private interface Claimed extends Trait {
    }

    /** Marker supertype and subtype for the subtype-aware claiming tests. */
    private interface Super extends Trait {
    }

    private interface Sub extends Super {
    }

    /** Target already claiming the subtype - the supertype is implied. */
    private static class SubTargetMarker implements Callable<String>, Sub {
        @Override
        public String call() {
            return "target";
        }
    }

    /** Source claiming a marker next to a non-trait interface and an implementation. */
    private static class MarkerSource implements Claimed, Supplier<String> {
        @Override
        public String get() {
            return "source";
        }
    }

    /** Source claiming only interfaces that carry implementations - none of them traits. */
    private static class CapabilitySource implements Cancelable {

    }

    /** Target already claiming the marker - nothing may be added to it. */
    private static class ClaimedTarget implements Callable<String>, Claimed {
        @Override
        public String call() {
            return "target";
        }
    }

    private static class Target implements Callable<String> {
        @Override
        public String call() {
            return "target";
        }
    }

    /** Interface claimed only by a superclass - a proxy target must still reproduce it. */
    private interface BaseOnly {
        String run();
    }

    private static class BaseTarget implements BaseOnly {
        @Override
        public String run() {
            return "base";
        }
    }

    /** Target declaring its capability directly, the interface-typed base above it. */
    private static class SubTarget extends BaseTarget implements Supplier<String> {
        @Override
        public String get() {
            return "sub";
        }
    }

    /** Source claiming a marker through its superclass only. */
    private static class MarkerBase implements Claimed { }

    private static class MarkerSub extends MarkerBase { }

    @Test
    void copyTraitsAddsMarkersOnly() {
        Callable<String> proxy = Traits.copyTraits(new MarkerSource(), new Target());
        assertThat(proxy)
                .as("Marker trait was claimed").isInstanceOf(Claimed.class)
                .as("Proxy is still the target's interface").isInstanceOf(Callable.class)
                .as("Supplier is not a trait and is not copied").isNotInstanceOf(Supplier.class);
    }

    @Test
    void proxyDelegatesEveryCallToTarget() throws Exception {
        Callable<?> proxy = Traits.copyTraits(new MarkerSource(), new Target());
        assertThat(proxy.call())
                .as("No implementation is routed through the source - target answers")
                .isEqualTo("target");
    }

    @Test
    void proxyRethrowsTargetExceptionsUnwrapped() {
        var proxy = Traits.copyTraits(new MarkerSource(), (Callable<String>) () -> {
            throw new IllegalStateException("boom");
        });
        assertThatThrownBy(proxy::call)
                .as("The proxy must throw what the target threw, not its wrapper")
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("boom");
    }

    @Test
    void alreadyClaimedMarkersAreSkipped() {
        var target = new ClaimedTarget();
        assertThat(Traits.copyTraits(new MarkerSource(), target))
                .as("Target claims the marker already - returned as is")
                .isSameAs(target);
    }

    @Test
    void capabilityInterfacesAreNotTraits() {
        var target = new Target();
        var proxy = Traits.copyTraits(new CapabilitySource(), target);
        assertThat(proxy)
                .as("Cancelable carries an implementation - never copied").isNotInstanceOf(Cancelable.class);
        assertThat(proxy)
                .as("Nothing to add - the target itself is returned")
                .isSameAs(target);
    }

    @Test
    void markersOfAProxySourceAreRecognized() {
        Callable<String> inner = Traits.copyTraits(new MarkerSource(), new Target());
        Callable<String> outer = Traits.copyTraits(inner, new Target());
        assertThat(outer)
                .as("Markers claimed by a proxy source are recognized and claimed again")
                .isInstanceOf(Claimed.class);
    }

    @Test
    void proxyReproducesInheritedTargetInterfaces() {
        BaseOnly target = new SubTarget(); // BaseOnly comes only via BaseTarget
        BaseOnly proxy = Traits.copyTraits(new MarkerSource(), target);
        assertThat(proxy)
                .as("Interface from the superclass is reproduced on the proxy").isInstanceOf(BaseOnly.class)
                .as("Marker of the source is claimed").isInstanceOf(Claimed.class);
        assertThat(proxy.run()).as("Call reaches the target").isEqualTo("base");
    }

    @Test
    void inheritedSourceMarkersAreClaimed() {
        Callable<String> proxy = Traits.copyTraits(new MarkerSub(), new Target());
        assertThat(proxy)
                .as("Marker declared by a superclass of the source is still claimed")
                .isInstanceOf(Claimed.class);
    }

    @Test
    void addingASupertypeOfAnAlreadyClaimedSubtypeIsANoOp() {
        var target = new SubTargetMarker();
        assertThat(Traits.addTraits(target, Super.class))
                .as("Target claims Sub, which is a Super - nothing to add")
                .isSameAs(target);
    }

    @Test
    void subtypeAwareClaimingDropsSubsumedSupertypes() {
        Callable<String> proxy = Traits.addTraits(new Target(), Super.class, Sub.class);
        assertThat(proxy)
                .as("The claimed subtype still makes the supertype present")
                .isInstanceOf(Sub.class)
                .isInstanceOf(Super.class);
        assertThat(proxy.getClass().getInterfaces())
                .as("Only the subtype is claimed - the supertype it subsumes is dropped")
                .contains(Sub.class)
                .doesNotContain(Super.class);
    }

    @Test
    void shutdownSubsumesCanceling() {
        Callable<String> proxy = Traits.addTraits(new Target(), Canceling.class, Shutdown.class);
        assertThat(proxy)
                .as("Shutdown implies its supertype through its hierarchy")
                .isInstanceOf(Shutdown.class)
                .isInstanceOf(Canceling.class);
        assertThat(proxy.getClass().getInterfaces())
                .as("Only Shutdown is claimed; Canceling is implied")
                .contains(Shutdown.class)
                .doesNotContain(Canceling.class);
    }

    @Test
    void shutdownNoLongerImpliesSync() {
        Callable<String> proxy = Traits.addTraits(new Target(), Shutdown.class);
        assertThat(proxy)
                .as("a terminal carrier stays asynchronous")
                .isInstanceOf(Shutdown.class)
                .isNotInstanceOf(Sync.class);
    }

}
