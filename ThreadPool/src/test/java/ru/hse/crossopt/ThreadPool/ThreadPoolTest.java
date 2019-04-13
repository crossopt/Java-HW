package ru.hse.crossopt.ThreadPool;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ThreadPoolTest {
    @Test
    void testBadThreadAmount() {
        assertThrows(IllegalArgumentException.class, () -> new ThreadPool<Integer>(0));
        assertThrows(IllegalArgumentException.class, () -> new ThreadPool<String>(-7));
    }

    @Test
    void testOneThreadSimpleTasksPrimitiveTypes() throws LightExecutionException {
        ThreadPool<Integer> pool = new ThreadPool<>(1);
        LightFuture<Integer> sumTask = pool.add(() -> 1 + 1);
        LightFuture<Integer> multiplyTask = pool.add(() -> 2 * 2);
        assertEquals(Integer.valueOf(2), sumTask.get());
        assertEquals(Integer.valueOf(4), multiplyTask.get());
    }

    @Test
    void testManyThreadsSimpleTasksPrimitiveTypes() throws LightExecutionException {
        ThreadPool<Integer> pool = new ThreadPool<>(10);
        LightFuture<Integer> sumTask = pool.add(() -> 1 + 1);
        LightFuture<Integer> multiplyTask = pool.add(() -> 2 * 2);
        assertEquals(Integer.valueOf(2), sumTask.get());
        assertEquals(Integer.valueOf(4), multiplyTask.get());
    }

    @Test
    void testOneThreadSimpleTasksNonPrimitiveTypesMultipleCalls() throws LightExecutionException {
        ThreadPool<String> pool = new ThreadPool<>(1);
        LightFuture<String> sumTask = pool.add(() -> Integer.valueOf(1 + 1).toString());
        LightFuture<String> multiplyTask = pool.add(() -> Integer.valueOf(2 * 2).toString());
        assertEquals("2", sumTask.get());
        assertEquals("4", multiplyTask.get());
        assertEquals("4", multiplyTask.get());
        assertEquals("4", multiplyTask.get());
        assertEquals("2", sumTask.get());
        assertEquals("2", sumTask.get());
        assertEquals("4", multiplyTask.get());
    }

    @Test
    void testManyThreadsSimpleTasksNonPrimitiveTypesMultipleCalls() throws LightExecutionException {
        ThreadPool<String> pool = new ThreadPool<>(10);
        LightFuture<String> sumTask = pool.add(() -> Integer.valueOf(1 + 1).toString());
        LightFuture<String> multiplyTask = pool.add(() -> Integer.valueOf(2 * 2).toString());
        assertEquals("2", sumTask.get());
        assertEquals("4", multiplyTask.get());
        assertEquals("4", multiplyTask.get());
        assertEquals("4", multiplyTask.get());
        assertEquals("2", sumTask.get());
        assertEquals("2", sumTask.get());
        assertEquals("4", multiplyTask.get());
    }

    @Test
    void testOneThreadException() {
        ThreadPool<String> pool = new ThreadPool<>(1);
        LightFuture<String> badTask = pool.add(() -> {
            throw new RuntimeException();
        });
        assertThrows(LightExecutionException.class, badTask::get);
        assertThrows(LightExecutionException.class, badTask::get);
    }

    @Test
    void testManyThreadsException() {
        ThreadPool<String> pool = new ThreadPool<>(15);
        for (int i = 0; i < 20; i++) {
            pool.add(() -> "good task");
        }
        LightFuture<String> badTask = pool.add(() -> {
            throw new RuntimeException();
        });
        assertThrows(LightExecutionException.class, badTask::get);
        assertThrows(LightExecutionException.class, badTask::get);
    }

    @Test
    void testOneThreadExceptionInThenApply() {
        ThreadPool<String> pool = new ThreadPool<>(1);
        for (int i = 0; i < 20; i++) {
            pool.add(() -> "good task");
        }
        LightFuture<String> firstTask = pool.add(() -> null);
        LightFuture<String> badTask = firstTask.thenApply(String::toLowerCase);
        assertThrows(LightExecutionException.class, badTask::get);
        assertThrows(LightExecutionException.class, badTask::get);
    }

    @Test
    void testManyThreadsExceptionInThenApply() {
        ThreadPool<String> pool = new ThreadPool<>(15);
        LightFuture<String> firstTask = pool.add(() -> null);
        LightFuture<String> badTask = firstTask.thenApply(String::toLowerCase);
        assertThrows(LightExecutionException.class, badTask::get);
        assertThrows(LightExecutionException.class, badTask::get);
    }

    @Test
    @SuppressWarnings("InfiniteLoopStatement")
    void testOneThreadIsReady() throws LightExecutionException {
        ThreadPool<String> pool = new ThreadPool<>(1);
        LightFuture<String> firstTask = pool.add(() -> "task is ready");
        assertEquals("task is ready", firstTask.get());
        assertTrue(firstTask.isReady());
        LightFuture<String> secondTask = pool.add(() -> {
            while (true) {
                Thread.yield();
            }
        });
        assertFalse(secondTask.isReady());
    }

    @Test
    @SuppressWarnings("InfiniteLoopStatement")
    void testManyThreadsIsReady() throws LightExecutionException {
        ThreadPool<String> pool = new ThreadPool<>(10);
        LightFuture<String> firstTask = pool.add(() -> "task is ready");
        assertEquals("task is ready", firstTask.get());
        assertTrue(firstTask.isReady());
        LightFuture<String> secondTask = pool.add(() -> {
            while (true) {
                Thread.yield();
            }
        });
        assertFalse(secondTask.isReady());
    }

    @Test
    void testCorrectAmountInPoolOneThread() throws NoSuchFieldException, IllegalAccessException {
        ThreadPool<Double> pool = new ThreadPool<>(1);

        for (int i = 0; i < 4; i++) {
            pool.add(() -> 3.14);
        }
        Field threads = pool.getClass().getDeclaredField("threads");
        threads.setAccessible(true);
        int threadsAlive = 0;
        for (var thread : (Thread[]) threads.get(pool)) {
            if (thread.isAlive()) {
                threadsAlive++;
            }
        }
        assertTrue(1 <= threadsAlive);
    }

    @Test
    void testCorrectAmountInPoolManyThreads() throws NoSuchFieldException, IllegalAccessException {
        ThreadPool<Double> pool = new ThreadPool<>(10);
        for (int i = 0; i < 8; i++) {
            pool.add(() -> {
                double x = 0.0;
                for (int count = 0; count < 1000000000; count++) {
                    x += count;
                }
                return x;
            });
        }
        Field threads = pool.getClass().getDeclaredField("threads");
        threads.setAccessible(true);
        int threadsAlive = 0;
        for (var thread : (Thread[]) threads.get(pool)) {
            if (thread.isAlive()) {
                threadsAlive++;
            }
        }
        assertTrue(10 <= threadsAlive);
    }

    @Test
    void testShutdownOneThreadDoesEndTasks() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        ThreadPool<Double> pool = new ThreadPool<>(1);

        for (int i = 0; i < 3; i++) {
            pool.add(() -> 0.0);
        }
        Field threads = pool.getClass().getDeclaredField("threads");
        threads.setAccessible(true);
        pool.shutdown();
        Thread.sleep(5000);
        for (var thread : (Thread[]) threads.get(pool)) {
            assertFalse(thread.isAlive());
        }
    }

    @Test
    void testShutdownManyThreadsDoesEndTasks() throws NoSuchFieldException, InterruptedException, IllegalAccessException {
        ThreadPool<Double> pool = new ThreadPool<>(3);

        for (int i = 0; i < 3; i++) {
            pool.add(() -> 1.0);
        }
        Field threads = pool.getClass().getDeclaredField("threads");
        threads.setAccessible(true);
        pool.shutdown();
        Thread.sleep(5000);
        for (var thread : (Thread[]) threads.get(pool)) {
            assertFalse(thread.isAlive());
        }
    }

    @Test
    void testShutdownOneThreadDoesntWorkNewTask() throws InterruptedException {
        ThreadPool<String> pool = new ThreadPool<>(1);
        for (int i = 0; i < 5; i++) {
            pool.add(() -> "2");
        }
        pool.shutdown();
        Thread.sleep(1000);
        LightFuture<String> task = pool.add(() -> "Task after interrupt");
        Thread.sleep(1000);
        assertFalse(task.isReady());
    }

    @Test
    void testShutdownManyThreadsDoesntWorkNewTask() throws InterruptedException {
        ThreadPool<String> pool = new ThreadPool<>(7);
        for (int i = 0; i < 2; i++) {
            pool.add(() -> "1.0");
        }
        pool.shutdown();
        Thread.sleep(1000);
        LightFuture<String> task = pool.add(() -> "Task after interrupt");
        Thread.sleep(1000);
        assertFalse(task.isReady());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testThenApplyOneThreadSimple() throws LightExecutionException {
        ThreadPool<Integer> pool = new ThreadPool<>(1);
        LightFuture<Integer> firstTask = pool.add(() -> 2);
        LightFuture<Integer>[] dependentTasks = new LightFuture[10];
        for (int i = 0; i < 10; i++) {
            final int ii = i;
            dependentTasks[i] = firstTask.thenApply(x -> x + ii * 2);
        }
        assertEquals(Integer.valueOf(2), firstTask.get());
        for (int i = 0; i < 10; i++) {
            assertEquals(Integer.valueOf(2 * (i + 1)), dependentTasks[i].get());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void testThenApplyManyThreadsSimple() throws LightExecutionException {
        ThreadPool<Integer> pool = new ThreadPool<>(1);
        LightFuture<Integer> firstTask = pool.add(() -> 2);
        LightFuture<Integer>[] dependentTasks = new LightFuture[10];
        for (int i = 0; i < 10; i++) {
            final int ii = i;
            dependentTasks[i] = firstTask.thenApply(x -> x + ii * 2);
        }
        assertEquals(Integer.valueOf(2), firstTask.get());
        for (int i = 0; i < 10; i++) {
            assertEquals(Integer.valueOf(2 * (i + 1)), dependentTasks[i].get());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void testThenApplyOneThreadConsecutive() throws LightExecutionException {
        ThreadPool<Integer> pool = new ThreadPool<>(1);
        LightFuture<Integer>[] dependentTasks = new LightFuture[10];
        dependentTasks[0] = pool.add(() -> 1);
        for (int i = 1; i < 10; i++) {
            dependentTasks[i] = dependentTasks[i - 1].thenApply(x -> x * 2);
        }
        for (int i = 0; i < 10; i++) {
            assertEquals(Integer.valueOf(1 << i), dependentTasks[i].get());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void testThenApplyManyThreadsConsecutive() throws LightExecutionException {
        ThreadPool<Integer> pool = new ThreadPool<>(15);
        LightFuture<Integer>[] dependentTasks = new LightFuture[10];
        dependentTasks[0] = pool.add(() -> 1);
        for (int i = 1; i < 10; i++) {
            dependentTasks[i] = dependentTasks[i - 1].thenApply(x -> x * 2);
        }
        for (int i = 0; i < 10; i++) {
            assertEquals(Integer.valueOf(1 << i), dependentTasks[i].get());
        }
    }

    @Test
    void testOneThreadSupplierWithDifferingValues() throws LightExecutionException {
        Random random = new Random(179);
        ThreadPool<Integer> pool = new ThreadPool<>(1);
        LightFuture<Integer> sumTask = pool.add(random::nextInt);
        Integer result = sumTask.get();
        for (int i = 0; i < 10; i++) {
            assertEquals(result, sumTask.get());
        }
    }

    @Test
    void testManyThreadsSupplierWithDifferingValues() throws LightExecutionException {
        Random random = new Random(179);
        ThreadPool<Integer> pool = new ThreadPool<>(15);
        LightFuture<Integer> sumTask = pool.add(random::nextInt);
        Integer result = sumTask.get();
        for (int i = 0; i < 25; i++) {
            assertEquals(result, sumTask.get());
        }
    }

    @Test
    void testOneThreadThenApplyForFailedTask() {
        ThreadPool<String> pool = new ThreadPool<>(1);
        LightFuture<String> badTask = pool.add(() -> {
            throw new RuntimeException();
        });
        LightFuture<String> secondBadTask = badTask.thenApply(x -> x + "!");
        assertThrows(LightExecutionException.class, badTask::get);
        assertThrows(LightExecutionException.class, secondBadTask::get);
    }

    @Test
    void testManyThreadsThenApplyForFailedTask() {
        ThreadPool<String> pool = new ThreadPool<>(5);
        LightFuture<String> badTask = pool.add(() -> {
            throw new RuntimeException();
        });
        LightFuture<String> secondBadTask = badTask.thenApply(x -> x + "!");
        assertThrows(LightExecutionException.class, badTask::get);
        assertThrows(LightExecutionException.class, secondBadTask::get);
        assertThrows(LightExecutionException.class, secondBadTask::get);
    }
}
