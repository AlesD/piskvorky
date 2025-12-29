package cz.doleckovi.piskvorky.gtp.command.trait;

@FunctionalInterface
public interface WithId extends Trait {
    String id();
}
