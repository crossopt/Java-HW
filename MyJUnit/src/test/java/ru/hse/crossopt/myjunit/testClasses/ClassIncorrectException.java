package ru.hse.crossopt.myjunit.testClasses;

import ru.hse.crossopt.myjunit.annotations.Test;

import static com.google.common.base.Preconditions.checkArgument;

public class ClassIncorrectException {
    private void methodThatThrowsException(int n) {
        checkArgument(n < 0, "Hi!");
    }

    @Test(expected = IllegalStateException.class)
    void testMethod() {
        methodThatThrowsException(5);
    }
}
