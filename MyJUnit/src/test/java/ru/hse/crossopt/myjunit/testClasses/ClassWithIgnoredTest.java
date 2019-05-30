package ru.hse.crossopt.myjunit.testClasses;

import ru.hse.crossopt.myjunit.annotations.Test;

public class ClassWithIgnoredTest {
    @Test
    public void runningTest() {
    }

    @Test(ignore = "doesn't work")
    private boolean ignoredTest() {
        throw new RuntimeException("This test should not run!");
    }
}
