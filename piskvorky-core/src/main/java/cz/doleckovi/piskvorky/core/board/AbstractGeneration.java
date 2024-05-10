package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.board.Generation;

public abstract class AbstractGeneration implements Generation {

    protected int age;

    protected AbstractGeneration(int age) {
        assert age >= 0 : "Negative age";
        this.age = age;
    }

    @Override
    public int getAge() {
        return age;
    }

}
