package cz.doleckovi.piskvorky.api.board;

public interface Generation {

    int getAge();

    default boolean isOlderThan(Generation other) {
        return getAge() < other.getAge();
    }

    default boolean isSameGenerationAs(Generation other) {
        return getAge() == other.getAge();
    }

}
