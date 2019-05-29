package ru.hse.crossopt.myjunit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hse.crossopt.myjunit.testClasses.MultipleAnnotationsClass;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TestRunnerTest {
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testMultipleAnnotations() {
        assertThrows(MyJUnitException.class, () -> TestRunner.runTests(MultipleAnnotationsClass.class));
    }

    @Test
    void testThrowsException() {
    }

    @Test
    void testThrowsIncorrectException() {
    }

    @Test
    void testShouldThrowException() {
    }

    @Test
    void testNoExceptions() {
    }

    @Test
    void testAllMethodsInOneClass() {
    }

    @Test
    void testEmpty() {
    }
}