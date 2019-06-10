package ru.hse.crossopt.myjunit.testClasses;

import ru.hse.crossopt.myjunit.annotations.Test;
import ru.hse.crossopt.myjunit.annotations.Before;
import ru.hse.crossopt.myjunit.annotations.After;
import ru.hse.crossopt.myjunit.annotations.BeforeClass;
import ru.hse.crossopt.myjunit.annotations.AfterClass;

public class BigClass {
    @BeforeClass
    void firstBeforeClass() {
        System.out.println("First BeforeClass ran.");
    }

    @BeforeClass
    void secondBeforeClass() {
        System.out.println("Second BeforeClass ran.");
    }

    @Test(ignore = "ignore this")
    void notATest() {
        System.out.println("Ignored test ran.");
    }

    @Before
    void before() {
        System.out.println("Before ran.");
    }

    @Test
    void aTest() {
        System.out.println("One test ran.");
    }

    @Test
    void anotherTest() {
        System.out.println("Another test ran.");
    }

    @After
    void after() {
        System.out.println("After ran.");
    }

    @AfterClass
    void afterClass() {
        System.out.println("AfterClass ran.");
    }
}
