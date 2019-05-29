package ru.hse.crossopt.myjunit.testClasses;

import ru.hse.crossopt.myjunit.annotations.AfterClass;
import ru.hse.crossopt.myjunit.annotations.Test;

public class MultipleAnnotationsClass {
    int five;
    @Test
    @AfterClass
    void testAfterEverythingElse() {
        five = 5;
    }
}
