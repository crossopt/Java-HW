package ru.hse.crossopt.ThreadPool;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ThreadPoolTest {
    @Test
    void testBadThreadAmount() {
        assertThrows(IllegalArgumentException.class, () -> new ThreadPool(0));
        assertThrows(IllegalArgumentException.class, () -> new ThreadPool(-7));
    }

    @Test
    void testOneThreadSimpleTasksPrimitiveTypes() throws LightExecutionException {
        var pool = new ThreadPool(1);
        LightFuture<Integer> sumTask = pool.add(() -> 1 + 1);
        LightFuture<Integer> multiplyTask = pool.add(() -> 2 * 2);
        assertEquals(Integer.valueOf(2), sumTask.get());
        assertEquals(Integer.valueOf(4), multiplyTask.get());
    }

    @Test
    void testManyThreadsSimpleTasksPrimitiveTypes() throws LightExecutionException {
        var pool = new ThreadPool(10);
        LightFuture<Integer> sumTask = pool.add(() -> 1 + 1);
        LightFuture<Integer> multiplyTask = pool.add(() -> 2 * 2);
        assertEquals(Integer.valueOf(2), sumTask.get());
        assertEquals(Integer.valueOf(4), multiplyTask.get());
    }

    @Test
    void testOneThreadSimpleTasksNonPrimitiveTypesMultipleCalls() throws LightExecutionException {
        var pool = new ThreadPool(1);
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
        var pool = new ThreadPool(10);
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
        var pool = new ThreadPool(1);
        LightFuture<String> badTask = pool.add(() -> {
            throw new RuntimeException();
        });
        assertThrows(LightExecutionException.class, badTask::get);
        assertThrows(LightExecutionException.class, badTask::get);
    }

    @Test
    void testManyThreadsException() {
        var pool = new ThreadPool(15);
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
        var pool = new ThreadPool(1);
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
        var pool = new ThreadPool(15);
        LightFuture<String> firstTask = pool.add(() -> null);
        LightFuture<String> badTask = firstTask.thenApply(String::toLowerCase);
        assertThrows(LightExecutionException.class, badTask::get);
        assertThrows(LightExecutionException.class, badTask::get);
    }

    @Test
    @SuppressWarnings("InfiniteLoopStatement")
    void testOneThreadIsReady() throws LightExecutionException {
        var pool = new ThreadPool(1);
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
        var pool = new ThreadPool(10);
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
        var pool = new ThreadPool(1);
        for (int i = 0; i < 4; i++) {
            pool.add(() -> 3.14);
        }
        var threads = pool.getClass().getDeclaredField("threads");
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
        var pool = new ThreadPool(10);
        for (int i = 0; i < 8; i++) {
            pool.add(() -> {
                double x = 0.0;
                for (int count = 0; count < 1000000000; count++) {
                    x += count;
                }
                return x;
            });
        }
        var threads = pool.getClass().getDeclaredField("threads");
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
        var pool = new ThreadPool(1);
        for (int i = 0; i < 3; i++) {
            pool.add(() -> 0.0);
        }
        var threads = pool.getClass().getDeclaredField("threads");
        threads.setAccessible(true);
        pool.shutdown();
        Thread.sleep(5000);
        for (var thread : (Thread[]) threads.get(pool)) {
            assertFalse(thread.isAlive());
        }
    }

    @Test
    void testShutdownManyThreadsDoesEndTasks() throws NoSuchFieldException, InterruptedException, IllegalAccessException {
        var pool = new ThreadPool(3);
        for (int i = 0; i < 3; i++) {
            pool.add(() -> 1.0);
        }
        var threads = pool.getClass().getDeclaredField("threads");
        threads.setAccessible(true);
        pool.shutdown();
        Thread.sleep(5000);
        for (var thread : (Thread[]) threads.get(pool)) {
            assertFalse(thread.isAlive());
        }
    }

    @Test
    void testShutdownOneThreadDoesntWorkNewTask() throws InterruptedException {
        var pool = new ThreadPool(1);
        for (int i = 0; i < 5; i++) {
            pool.add(() -> "2");
        }
        pool.shutdown();
        Thread.sleep(1000);
        assertThrows(IllegalStateException.class, () -> pool.add(() -> "Task after interrupt"));
    }

    @Test
    void testShutdownManyThreadsDoesntWorkNewTask() throws InterruptedException {
        var pool = new ThreadPool(7);
        for (int i = 0; i < 2; i++) {
            pool.add(() -> "1.0");
        }
        pool.shutdown();
        Thread.sleep(1000);
        assertThrows(IllegalStateException.class, () -> pool.add(() -> "Task after interrupt"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testThenApplyOneThreadSimple() throws LightExecutionException {
        var pool = new ThreadPool(1);
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
        var pool = new ThreadPool(1);
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
        var pool = new ThreadPool(1);
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
        var pool = new ThreadPool(15);
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
        var random = new Random(179);
        var pool = new ThreadPool(1);
        LightFuture<Integer> sumTask = pool.add(random::nextInt);
        Integer result = sumTask.get();
        for (int i = 0; i < 10; i++) {
            assertEquals(result, sumTask.get());
        }
    }

    @Test
    void testManyThreadsSupplierWithDifferingValues() throws LightExecutionException {
        var random = new Random(179);
        var pool = new ThreadPool(15);
        LightFuture<Integer> sumTask = pool.add(random::nextInt);
        Integer result = sumTask.get();
        for (int i = 0; i < 25; i++) {
            assertEquals(result, sumTask.get());
        }
    }

    @Test
    void testOneThreadThenApplyForFailedTask() {
        var pool = new ThreadPool(1);
        LightFuture<String> badTask = pool.add(() -> {
            throw new RuntimeException();
        });
        LightFuture<String> secondBadTask = badTask.thenApply(x -> x + "!");
        assertThrows(LightExecutionException.class, badTask::get);
        assertThrows(LightExecutionException.class, secondBadTask::get);
    }

    @Test
    void testManyThreadsThenApplyForFailedTask() {
        var pool = new ThreadPool(5);
        LightFuture<String> badTask = pool.add(() -> {
            throw new RuntimeException();
        });
        LightFuture<String> secondBadTask = badTask.thenApply(x -> x + "!");
        assertThrows(LightExecutionException.class, badTask::get);
        assertThrows(LightExecutionException.class, secondBadTask::get);
        assertThrows(LightExecutionException.class, secondBadTask::get);
    }

    @Test
    void testOneThreadThenApplyAfterGetCalculated() throws LightExecutionException {
        var pool = new ThreadPool(1);
        LightFuture<String> task = pool.add(() -> "0");
        assertEquals("0", task.get());
        LightFuture<String> secondTask = task.thenApply(x -> x + "!");
        LightFuture<String> thirdTask = task.thenApply(x -> x + "?");
        LightFuture<String> fourthTask = task.thenApply(x -> x + "...");
        assertEquals("0!", secondTask.get());
        assertEquals("0?", thirdTask.get());
        assertEquals("0...", fourthTask.get());
    }

    @Test
    void testManyThreadsThenApplyAfterGetCalculated() throws LightExecutionException {
        var pool = new ThreadPool(5);
        LightFuture<String> task = pool.add(() -> "0");
        assertEquals("0", task.get());
        LightFuture<String> secondTask = task.thenApply(x -> x + "!");
        LightFuture<String> thirdTask = task.thenApply(x -> x + "?");
        LightFuture<String> fourthTask = task.thenApply(x -> x + "...");
        assertEquals("0!", secondTask.get());
        assertEquals("0?", thirdTask.get());
        assertEquals("0...", fourthTask.get());
    }

    @Test
    void testOneThreadLongTaskWithThenApply() throws LightExecutionException {
        var pool = new ThreadPool(1);
        LightFuture<String> task = pool.add(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return "1";
            }
            return "0";
        });
        assertEquals("0", task.get());
        LightFuture<String> secondTask = task.thenApply(x -> x + "!");
        LightFuture<String> thirdTask = task.thenApply(x -> x + "?");
        LightFuture<String> fourthTask = task.thenApply(x -> x + "...");
        assertEquals("0!", secondTask.get());
        assertEquals("0?", thirdTask.get());
        assertEquals("0...", fourthTask.get());
    }

    @Test
    void testManyThreadsLongTaskWithThenApply() throws LightExecutionException {
        var pool = new ThreadPool(15);
        LightFuture<String> task = pool.add(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return "1";
            }
            return "0";
        });
        assertEquals("0", task.get());
        LightFuture<String> secondTask = task.thenApply(x -> x + "!");
        LightFuture<String> thirdTask = task.thenApply(x -> x + "?");
        LightFuture<String> fourthTask = task.thenApply(x -> x + "...");
        assertEquals("0!", secondTask.get());
        assertEquals("0?", thirdTask.get());
        assertEquals("0...", fourthTask.get());
    }

    @Test
    void testThenApplyDifferentTypeFunctions() throws LightExecutionException {
        var pool = new ThreadPool(5);
        LightFuture<Integer> task = pool.add(() -> 0);
        LightFuture<String> secondTask = task.thenApply(x -> Integer.valueOf(x + 1).toString());
        LightFuture<String> thirdTask = task.thenApply(Object::toString);
        assertEquals(Integer.valueOf(0), task.get());
        assertEquals("1", secondTask.get());
        assertEquals("0", thirdTask.get());
    }
}
