package ru.hse.crossopt.myjunit;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hse.crossopt.myjunit.testClasses.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

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
        assertDoesNotThrow(() -> TestRunner.runTests(ClassTestThrowsStuff.class));
        checkOutputTestPassed("testMethod");
        checkEndOutputResult(1, 1, 0, 0);
    }

    @Test
    void testThrowsIncorrectException() {
        assertDoesNotThrow(() -> TestRunner.runTests(ClassIncorrectException.class));
        checkOutputTestFailed("testMethod", "java.lang.IllegalArgumentException: Hi!");
        checkEndOutputResult(1, 0, 1, 0);
    }

    @Test
    void testShouldThrowExceptionButDoesNot() {
        assertDoesNotThrow(() -> TestRunner.runTests(ClassShouldThrowStuff.class));
        checkOutputTestFailed("testThrowsException", "expected exception java.lang.IllegalArgumentException");
        checkEndOutputResult(1, 0, 1, 0);
    }

    @Test
    void testNoExceptions() {
        assertDoesNotThrow(() -> TestRunner.runTests(ClassTestDoesNotThrowAnything.class));
        checkOutputTestPassed("publicTest");
        checkOutputTestPassed("privateTest");
        checkEndOutputResult(2, 2, 0, 0);
    }

    @Test
    void testIgnoreOneTest() {
        assertDoesNotThrow(() -> TestRunner.runTests(ClassWithIgnoredTest.class));
        checkOutputTestPassed("runningTest");
        checkOutputTestIgnored("ignoredTest", "doesn't work");
        checkEndOutputResult(1, 1, 0, 1);
    }

    @Test
    void testAllMethodsInOneClassRun() {
        assertDoesNotThrow(() -> TestRunner.runTests(BigClass.class));
        checkOutputTestPassed("aTest");
        checkOutputTestPassed("anotherTest");
        checkOutputTestIgnored("notATest", "ignore this");
        checkEndOutputResult(2, 2, 0, 1);

        String output = outputStream.toString();
        assertEquals(1, StringUtils.countMatches(output, "First BeforeClass ran."));
        assertEquals(1, StringUtils.countMatches(output, "Second BeforeClass ran."));
        assertEquals(2, StringUtils.countMatches(output,"Before ran."));
        assertEquals(0, StringUtils.countMatches(output,"Ignored test ran."));
        assertEquals(1, StringUtils.countMatches(output, "One test ran."));
        assertEquals(1, StringUtils.countMatches(output, "Another test ran."));
        assertEquals(2, StringUtils.countMatches(output, "After ran."));
        assertEquals(1, StringUtils.countMatches(output, "AfterClass ran."));
    }

    @Test
    void testEmpty() {
        assertDoesNotThrow(() -> TestRunner.runTests(ClassWithoutTests.class));
        checkEndOutputResult(0, 0, 0, 0);
    }

    private void checkOutputTestPassed(String testName) {
        String output = outputStream.toString();
        assertTrue(output.contains("Test " + testName + " started."));
        assertTrue(output.contains("Test " + testName + " passed in "));
        assertFalse(output.contains("Test " + testName + " ignored"));
        assertFalse(output.contains("Test " + testName + " failed"));
    }

    private void checkOutputTestIgnored(String testName, String ignoreReason) {
        String output = outputStream.toString();
        assertTrue(output.contains("Test " + testName + " ignored. Reason: " + ignoreReason + ".\n"));
        assertFalse(output.contains("Test " + testName + " started"));
        assertFalse(output.contains("Test " + testName + " passed"));
        assertFalse(output.contains("Test " + testName + " failed"));
    }

    private void checkOutputTestFailed(String testName, String failMessage) {
        String output = outputStream.toString();
        assertTrue(output.contains("Test " + testName + " started."));
        assertTrue(output.contains("Test " + testName + " failed in "));
        assertTrue(output.contains("ms: " + failMessage));
        assertFalse(output.contains("Test " + testName + " passed"));
        assertFalse(output.contains("Test " + testName + " ignored"));
    }

    private void checkEndOutputResult(int expectedRan, int expectedPassed, int expectedFailed, int expectedIgnored) {
        String output = outputStream.toString();
        assertTrue(output.contains("Ran " + expectedRan + " tests."));
        assertTrue(output.contains("Passed " + expectedPassed + " tests."));
        assertTrue(output.contains("Failed " + expectedFailed + " tests."));
        assertTrue(output.contains("Ignored " + expectedIgnored + " tests."));
    }
}