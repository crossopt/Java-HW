package ru.hse.crossopt.myjunit.testClasses;

import ru.hse.crossopt.myjunit.annotations.Test;

import static com.google.common.base.Preconditions.checkArgument;

public class ClassTestThrowsStuff {
    private void methodThatThrowsException(int n) {
        checkArgument(n < 0, "Threw exception.");
    }

    @Test(expected = IllegalArgumentException.class)
    void testMethod() {
        methodThatThrowsException(5);
    }
}
