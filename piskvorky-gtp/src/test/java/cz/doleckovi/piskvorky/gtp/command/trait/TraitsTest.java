package cz.doleckovi.piskvorky.gtp.command.trait;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

class TraitsTest implements WithAssertions {

    private final static Object OBJECT = new Object();
    private final static Supplier<Object> SUPPLIER = () -> OBJECT;

    @Test
    void addId() {
        var proxy = Traits.addId(SUPPLIER, "id");
        assertThat(proxy).as("WithId trait was added").isInstanceOfSatisfying(WithId.class, withId ->
                        assertThat(withId.id())
                                .as("Added WithId trait works").isEqualTo("id"));
        checkProxyIsStillSupplier(proxy);
    }

    @Test
    void copyTraits() {
        var withId = Traits.addId(SUPPLIER, "id");
        var runnableFuture = new FutureTask<>(() -> null);
        var proxy = Traits.copyTraits(withId, runnableFuture);
        assertThat(proxy)
                .as("Supplier is not Trait and is not copied").isNotInstanceOf(Supplier.class)
                .as("WithId is trait and is copied").isInstanceOfSatisfying(WithId.class, idTrait ->
                        assertThat(idTrait.id())
                                .as("Copied WithId trait works").isEqualTo("id"))
                .as("Proxy is still RunnableFuture").isInstanceOf(RunnableFuture.class);
    }

    @Test
    void addInterface() {
        Function proxy = Traits.addInterface((Function<String, String>) String::toUpperCase, SUPPLIER, Function.class);
        assertThat(proxy)
                .as("Proxy has ne Function interfaces").isInstanceOfSatisfying(Function.class, function ->
                        assertThat(function.apply("test"))
                                .as("Added Function works").isEqualTo("TEST"))
                .as("Proxy is still Supplier").isInstanceOfSatisfying(Supplier.class, supplier ->
                        assertThat(supplier.get()).as("Proxied Supplier works").isSameAs(SUPPLIER.get()));
    }

    @Test
    void overrideInterface() {
        Supplier<Object> override = () -> "test";
        Object proxy = Traits.addInterfaces(override, SUPPLIER, Supplier.class);
        assertThat(proxy)
                .as("Proxy is still Supplier").isInstanceOfSatisfying(Supplier.class, supplier ->
                        assertThat(supplier.get()).as("Original Supplier implementation is overridden").isSameAs(supplier.get()));
    }

    @Test
    void getOrigin() {
        WithId withId = () -> "id";
        Object proxy = Traits.addInterface(withId, SUPPLIER, WithId.class);
        RunnableFuture runnableFuture = new FutureTask<>(() -> null);
        proxy = Traits.addInterfaces(proxy, runnableFuture, Supplier.class, WithId.class);
        assertThat(proxy)
                .as("Proxy is Supplier").isInstanceOf(Supplier.class)
                .as("Proxy is WithId").isInstanceOf(WithId.class)
                .as("Proxy is RunnableFuture").isInstanceOf(RunnableFuture.class);
        assertThat(Traits.getOrigin((Supplier) proxy, Supplier.class))
                .as("Supplier is originating from SUPPLIER").isSameAs(SUPPLIER);
        assertThat(Traits.getOrigin((WithId) proxy, WithId.class))
                .as("WithId is originating from withId lambda").isSameAs(withId);
        assertThat(Traits.getOrigin((RunnableFuture) proxy, RunnableFuture.class))
                .isSameAs(runnableFuture);
    }

    private void checkProxyIsStillSupplier(Object proxy) {
        assertThat(proxy)
                .as("Proxy is still Supplier").isInstanceOfSatisfying(Supplier.class, supplier ->
                        assertThat(supplier.get()).as("Proxied Supplier works").isSameAs(SUPPLIER.get()));
    }

}
