package ru.hse.crossopt.myjunit;

import ru.hse.crossopt.myjunit.annotations.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/**
 * Class that runs tests for a given class.
 * Methods with BeforeClass annotation are invoked before all test methods.
 * Methods with AfterClass annotation are invoked after all test methods.
 * Methods with Before annotation are invoked before each test method.
 * Methods with After annotation are invoked after each test method.
 */
public class TestRunner {
    /**
     * Runs all test for the given class.
     * @param args the name of the class for which tests should be run.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Require 1 argument: name of the class with tests to run.");
            return;
        }

        Class testClass;
        try {
            testClass = Class.forName(args[0]);
        } catch (ClassNotFoundException exception) {
            System.out.println("Error: class was not found.");
            return;
        }

        try {
            runTests(testClass);
        } catch (MyJUnitException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Runs all tests (with assorted after, afterClass, before, beforeClass methods) for the given class.
     * @param testClass a class for which to run methods.
     * @throws MyJUnitException if running the tests failed.
     */
    public static void runTests(Class<?> testClass) throws MyJUnitException {
        List<Method> beforeClassMethods = new ArrayList<>();
        List<Method> beforeMethods = new ArrayList<>();
        List<Method> afterClassMethods = new ArrayList<>();
        List<Method> afterMethods = new ArrayList<>();
        List<Method> testMethods = new ArrayList<>();
        for (var method : testClass.getDeclaredMethods()) {
            method.setAccessible(true);
            int foundAnnotations = 0;
            if (method.getAnnotation(Test.class) != null) {
                testMethods.add(method);
                foundAnnotations++;
            }
            if (method.getAnnotation(BeforeClass.class) != null) {
                beforeClassMethods.add(method);
                foundAnnotations++;
            }
            if (method.getAnnotation(AfterClass.class) != null) {
                afterClassMethods.add(method);
                foundAnnotations++;
            }
            if (method.getAnnotation(Before.class) != null) {
                beforeMethods.add(method);
                foundAnnotations++;
            }
            if (method.getAnnotation(After.class) != null) {
                afterMethods.add(method);
                foundAnnotations++;
            }
            if (foundAnnotations > 1) {
                throw new MyJUnitException("More than one method type annotation at " + method.getName());
            }
        }

        var status = new TestStatus();
        runMethods(testClass, beforeClassMethods);
        runTestMethods(testClass, status, beforeMethods, testMethods, afterMethods);
        runMethods(testClass, afterClassMethods);
        status.printResults();
    }

    private static void runMethods(Class<?> testClass, List<Method> methods) throws MyJUnitException {
        try {
            for (var method : methods) {
                method.invoke(testClass.getDeclaredConstructor().newInstance());
            }
        } catch (ReflectiveOperationException exception) {
            throw new MyJUnitException("Invoking methods for class failed.");
        }
    }

    private static void runTestMethods(Class<?> testClass, TestStatus testStatus, List<Method> beforeMethods,
                                       List<Method> testMethods, List<Method> afterMethods) throws MyJUnitException {
        for (var testMethod : testMethods) {
            var testAnnotation = testMethod.getAnnotation(Test.class);
            if (!testAnnotation.ignore().equals(Test.EMPTY_REASON)) {
                testStatus.ignore(testMethod.getName(), testAnnotation.ignore());
                continue;
            }
            runMethods(testClass, beforeMethods);
            boolean wasException = false;
            testStatus.start(testMethod.getName());
            try {
                testMethod.invoke(testClass.getDeclaredConstructor().newInstance());
            } catch (Throwable throwable) {
                wasException = true;
                if (throwable.getCause().getClass().equals(testAnnotation.expected())) {
                    testStatus.pass(testMethod.getName());
                } else {
                    testStatus.fail(testMethod.getName(),
                            throwable.getCause().getClass().getName() + ": " + throwable.getCause().getMessage());
                }
            }
            if (!wasException) {
                if (Test.DefaultException.class.equals(testAnnotation.expected())) {
                    testStatus.pass(testMethod.getName());
                } else {
                    testStatus.fail(testMethod.getName(), "expected exception " + testAnnotation.expected().getName());
                }
            }
            runMethods(testClass, afterMethods);
        }
    }

    /** Class that stores current status of the tests, the amount of started, passed, failed and ignored tests. */
    private static class TestStatus {
        private int ignored;
        private int started;
        private int passed;
        private int failed;
        private long startTimeStamp;
        private long globalStartTime;

        private TestStatus() {
            globalStartTime = System.currentTimeMillis();
        }

        private void ignore(String testName, String ignoreReason) {
            ignored++;
            System.out.println(String.format("Test %s ignored. Reason: %s.", testName, ignoreReason));
        }

        private void start(String testName) {
            checkState(startTimeStamp == 0); //start and end of tests should alternate.
            started++;
            startTimeStamp = System.currentTimeMillis();
            System.out.println(String.format("Test %s started.", testName));
        }

        private void fail(String testName, String message) {
            checkState(startTimeStamp != 0); //start and end of tests should alternate.
            failed++;
            System.out.println(String.format("Test %s failed in %d ms: %s", testName,
                    System.currentTimeMillis() - startTimeStamp, message));
            startTimeStamp = 0;
        }

        private void pass(String testName) {
            checkState(startTimeStamp != 0); //start and end of tests should alternate.
            passed++;
            System.out.println(String.format("Test %s passed in %d ms.", testName,
                    System.currentTimeMillis() - startTimeStamp));
            startTimeStamp = 0;
        }

        private void printResults() {
            System.out.println(); //empty line to leave break between testing output and the final result.
            System.out.println(String.format("Ran %d tests.", started));
            System.out.println(String.format("Passed %d tests.", passed));
            System.out.println(String.format("Failed %d tests.", failed));
            System.out.println(String.format("Ignored %d tests.", ignored));
            System.out.println(String.format("Total time was %d ms.", System.currentTimeMillis() - globalStartTime));
        }
    }
}
