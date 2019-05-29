package ru.hse.crossopt.myjunit.testClasses;

import ru.hse.crossopt.myjunit.annotations.AfterClass;
import ru.hse.crossopt.myjunit.annotations.BeforeClass;

public class ClassWithoutTests {
    int five;

    @AfterClass
    private void afterClass() {
        five = 5;
    }

    @BeforeClass
    private void beforeClass() {
        five = 4;
    }
}
