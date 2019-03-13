package ru.hse.crossopt.qsort;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * Class that sorts given list using quick sort.
 * Sorted elements must implement the Comparable interface.
 * */
public class Qsort {
    /** Amount of elements after which parallel sort will not make new threads and use simple sort instead.  */
    private static final int AMOUNT_FOR_SIMPLE_SORT = 42;

    /**
     * Sorts the elements of given list via quick sort using multiple threads.
     * @param list a list to sort.
     * @param threadAmount the amount of threads to be used.
     * @param <T> the type of the list elements. Must implement the Comparable interface.
     * @throws IllegalArgumentException if thread amount is non-positive.
     */
    public static <T extends Comparable<? super T>> void parallelSort(@NotNull List<T> list, int threadAmount)
            throws IllegalArgumentException {
        if (threadAmount <= 0) {
            throw new IllegalArgumentException("Thread amount should be positive.");
        }

        ExecutorService threadPool = Executors.newFixedThreadPool(threadAmount);
        var latch = new CountDownLatch(list.size());
        threadPool.submit(new QsortTask<>(0, list.size(), list, latch, threadPool));
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to sort list.");
        }
    }

    /**
     * Sorts the given list via quick sort in one thread.
     * @param list a list to sort.
     * @param <T> the type of the list elements. Must implement the Comparable interface.
     */
    public static <T extends Comparable<? super T>> void simpleSort(@NotNull List<T> list) {
        simpleSort(list, 0, list.size());
    }

    /**
     * Sorts elements of the list at indices from left to right via quick sort in one thread.
     * @param list a list to sort.
     * @param left the left index of range to sort, inclusive.
     * @param right the right index of range to sort, non-inclusive.
     * @param <T> the type of the list elements. Must implement the Comparable interface.
     */
    private static <T extends Comparable<? super T>> void simpleSort(@NotNull List<T> list, int left, int right) {
        if (right - left <= 1) {
            return;
        }
        int middle = partition(list, left, right);
        simpleSort(list, left, middle);
        simpleSort(list, middle + 1, right);
    }

    /**
     * Partition the elements of a list at indices from left to right by comparison to a random element.
     * @param list a list to partition.
     * @param left the left index of range to partition, inclusive.
     * @param right the right index of range to partition, non-inclusive.
     * @param <T> the type of the list elements. Must implement the Comparable interface.
     * @return index of an element such that all elements to the left of it are not greater than it
     * and all elements to the right are not smaller than it.
     */
    private static <T extends Comparable<? super T>> int partition(@NotNull List<T> list, int left, int right) {
        int pivotIndex = (ThreadLocalRandom.current().nextInt(left, right));
        T pivotValue = list.get(pivotIndex);
        Collections.swap(list, pivotIndex, right - 1);
        int middle = left;
        for(int i = left; i < right; i++) {
            if(pivotValue.compareTo(list.get(i)) > 0) {
                Collections.swap(list, middle, i);
                middle++;
            }
        }
        Collections.swap(list, right - 1, middle);
        return middle;
    }

    private static class QsortTask<T extends Comparable<? super T>> implements Runnable {
        private final int left;
        private final int right;
        private final @NotNull List<T> list;
        private final @NotNull CountDownLatch latch;
        private final @NotNull ExecutorService threadPool;

        public QsortTask(int left, int right, @NotNull List<T> list,
                         @NotNull CountDownLatch latch, @NotNull ExecutorService threadPool) {
            this.left = left;
            this.right = right;
            this.list = list;
            this.latch = latch;
            this.threadPool = threadPool;
        }

        /** Divides task (range to sort) into two smaller ones, submits them into the thread pool. */
        @Override
        public void run() {
            if (right - left <= AMOUNT_FOR_SIMPLE_SORT) {
                simpleSort(list, left, right);
                for (int i = left; i < right; i++) {
                    latch.countDown();
                }
                return;
            }

            int middle = partition(list, left, right);
            threadPool.submit(new QsortTask<>(left, middle, list, latch, threadPool));
            threadPool.submit(new QsortTask<>(middle + 1, right, list, latch, threadPool));
            latch.countDown();
        }
    }
}
