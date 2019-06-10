package ru.hse.crossopt.myjunit.testClasses;

import ru.hse.crossopt.myjunit.annotations.Test;

import static com.google.common.base.Preconditions.checkArgument;

public class ClassShouldThrowStuff {
    private void methodThatThrowsException(int n) {
        checkArgument(n < 0, "Illegal argument");
    }

    @Test(expected = IllegalArgumentException.class)
    void testThrowsException() {
        methodThatThrowsException(-1);
    }
}
