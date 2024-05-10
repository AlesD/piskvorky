package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.board.Generation;
import org.assertj.core.api.AbstractAssert;

public abstract class GenerationAssert<SELF extends GenerationAssert<SELF, ACTUAL>, ACTUAL extends Generation> extends AbstractAssert<SELF, ACTUAL> {

    protected GenerationAssert(final ACTUAL actual, final Class<?> selfType) {
        super(actual, selfType);
    }

    public SELF hasAge(int age) {
        isNotNull();
        if (actual.getAge() != age) {
            failWithMessage("Expected age %d", age);
        }
        return this.myself;
    }

}
