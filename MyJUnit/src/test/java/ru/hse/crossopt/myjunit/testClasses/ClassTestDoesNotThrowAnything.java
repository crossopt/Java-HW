package ru.hse.crossopt.myjunit.testClasses;

import ru.hse.crossopt.myjunit.annotations.Test;

public class ClassTestDoesNotThrowAnything {
    private static final int USELESS_CYCLE_AMOUNT = 10000;

    @Test
    public void publicTest() {
        for (int i = 0; i < USELESS_CYCLE_AMOUNT; i++) {
            assert i >= 0;
        }
    }

    @Test
    private boolean privateTest() {
        for (int i = 0; i < USELESS_CYCLE_AMOUNT; i++) {
            assert !(i < 0);
        }
        return true;
    }
}
