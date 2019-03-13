package ru.hse.crossopt.qsort;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class QsortTest {
    private static final double NANOS_IN_SECOND = 1000000000.0;
    private final Random random = new Random(179);
    private ArrayList<Integer> testInteger;
    private ArrayList<Integer> testIntegerCopy;

    void generateList(int length) {
        testInteger = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            testInteger.add(random.nextInt());
        }
        testIntegerCopy = new ArrayList<>(testInteger);
    }

    @Test
    void parallelSort_empty() {
        generateList(0);
        Qsort.parallelSort(testInteger, 2);
        Collections.sort(testIntegerCopy);
        assertArrayEquals(testIntegerCopy.toArray(), testInteger.toArray());
    }

    @Test
    void simpleSort_empty() {
        generateList(0);
        Qsort.simpleSort(testInteger);
        Collections.sort(testIntegerCopy);
        assertArrayEquals(testIntegerCopy.toArray(), testInteger.toArray());
    }

    @Test
    void parallelSort_oneElementOneThread() {
        generateList(1);
        Qsort.parallelSort(testInteger, 1);
        Collections.sort(testIntegerCopy);
        assertArrayEquals(testIntegerCopy.toArray(), testInteger.toArray());
    }

    @Test
    void parallelSort_oneElementManyThreads() {
        generateList(1);
        Qsort.parallelSort(testInteger, 5);
        assertArrayEquals(testIntegerCopy.toArray(), testInteger.toArray());
    }

    @Test
    void simpleSort_oneElement() {
        generateList(1);
        Qsort.simpleSort(testInteger);
        assertArrayEquals(testIntegerCopy.toArray(), testInteger.toArray());
    }

    @Test
    void parallelSort_fewElementsOneThread() {
        generateList(50);
        Qsort.parallelSort(testInteger, 1);
        Collections.sort(testIntegerCopy);
        assertArrayEquals(testIntegerCopy.toArray(), testInteger.toArray());
    }

    @Test
    void simpleSort_fewElements() {
        generateList(50);
        Qsort.simpleSort(testInteger);
        Collections.sort(testIntegerCopy);
        assertArrayEquals(testIntegerCopy.toArray(), testInteger.toArray());
    }

    @Test
    void parallelSort_fewElementsManyThreads() {
        generateList(50);
        Qsort.parallelSort(testInteger, 5);
        Collections.sort(testIntegerCopy);
        assertArrayEquals(testIntegerCopy.toArray(), testInteger.toArray());
    }

    @Test
    void parallelSort_manyElementsOneThread() {
        generateList(50000);
        Qsort.parallelSort(testInteger, 1);
        Collections.sort(testIntegerCopy);
        assertArrayEquals(testIntegerCopy.toArray(), testInteger.toArray());
    }

    @Test
    void simpleSort_manyElements() {
        generateList(50000);
        Qsort.simpleSort(testInteger);
        Collections.sort(testIntegerCopy);
        assertArrayEquals(testIntegerCopy.toArray(), testInteger.toArray());
    }

    @Test
    void parallelSort_manyElementsManyThreads() {
        generateList(50000);
        Qsort.parallelSort(testInteger, 5);
        Collections.sort(testIntegerCopy);
        assertArrayEquals(testIntegerCopy.toArray(), testInteger.toArray());
    }

    @Test
    void parallelSort_negativeThreads() {
        generateList(20);
        assertThrows(IllegalArgumentException.class, () -> Qsort.parallelSort(testInteger, -1));
    }

    @Test
    void parallelSort_sortStrings() {
        ArrayList<String> stringTest = new ArrayList<>();
        stringTest.add("test");
        stringTest.add("test");
        stringTest.add("also test");
        ArrayList<String> stringTestCopy = new ArrayList<>(stringTest);
        Qsort.parallelSort(stringTest, 1);
        Collections.sort(stringTestCopy);
        assertArrayEquals(stringTestCopy.toArray(), stringTest.toArray());
    }

    @Test
    void simpleSort_sortStrings() {
        ArrayList<String> stringTest = new ArrayList<>();
        stringTest.add("test");
        stringTest.add("test");
        stringTest.add("also test");
        ArrayList<String> stringTestCopy = new ArrayList<>(stringTest);
        Qsort.simpleSort(stringTest);
        Collections.sort(stringTestCopy);
        assertArrayEquals(stringTestCopy.toArray(), stringTest.toArray());
    }

    @Test
    void parallelSort_sortCustom() {
        ArrayList<MyClass> customTest = new ArrayList<>();
        customTest.add(new MyClass("test"));
        customTest.add(new MyClass("another test"));
        customTest.add(new MyClass("also test"));
        ArrayList<MyClass> customTestCopy = new ArrayList<>(customTest);
        Qsort.parallelSort(customTest, 2);
        Collections.sort(customTestCopy);
        assertArrayEquals(customTestCopy.toArray(), customTest.toArray());
    }

    @Test
    void simpleSort_sortCustom() {
        ArrayList<MyClass> customTest = new ArrayList<>();
        customTest.add(new MyClass("test"));
        customTest.add(new MyClass("another test"));
        customTest.add(new MyClass("also test"));
        ArrayList<MyClass> customTestCopy = new ArrayList<>(customTest);
        Qsort.simpleSort(customTest);
        Collections.sort(customTestCopy);
        assertArrayEquals(customTestCopy.toArray(), customTest.toArray());
    }

    @Test
    void parallelSort_testSpeed() {
        // on my system, starting from 50000 elements parallel sort is optimal
        for (int i = 1000; i <= 100000; i *= 10) {
            printSortResult(i, 10, false);
            printSortResult(5 * i, 10, false);
        }
    }

    private void printSortResult(int length, int runs, boolean printSlowTests) {
        long standardTime = 0;
        long parallelTime = 0;
        for (int i = 0; i < runs; ++i) {
            generateList(length);

            long standardStartTime = System.nanoTime();
            Qsort.simpleSort(testIntegerCopy);
            long standardStopTime = System.nanoTime();
            standardTime += standardStopTime - standardStartTime;

            long parallelStartTime = System.nanoTime();
            Qsort.parallelSort(testInteger, 4);
            long parallelStopTime = System.nanoTime();
            parallelTime += parallelStopTime - parallelStartTime;
        }

        if (printSlowTests || standardTime > parallelTime) {
            System.out.println("When sorting list with " + length + " elements");
            System.out.println("Standard sort of list takes on average " +
                    (standardTime) / (NANOS_IN_SECOND * runs) + " seconds");
            System.out.println("Parallel sort of list takes on average " +
                    (parallelTime) / (NANOS_IN_SECOND * runs) + " seconds");
        }
    }


    public class MyClass implements Comparable<MyClass> {
        String field;

        public MyClass(String field) {
            this.field = field;
        }

        @Override
        public int compareTo(@NotNull QsortTest.MyClass other) {
            return other.field.compareTo(field);
        }
    }
}