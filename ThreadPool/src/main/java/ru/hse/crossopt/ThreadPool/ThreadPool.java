package ru.hse.crossopt.ThreadPool;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.Supplier;

/** Thread pool class with fixed number of threads. */
public class ThreadPool<T> {
    private final @NotNull Thread[] threads;
    private final @NotNull LinkedList<ThreadPoolTask> taskQueue;
    private volatile boolean wasShutdown = false;

    /**
     * Creates a thread pool with the given number of threads.
     * @param threadAmount the amount of threads in the thread pool.
     * @throws IllegalArgumentException if the amount of threads is non-positive.
     */
    public ThreadPool(int threadAmount) {
        if (threadAmount <= 0) {
            throw new IllegalArgumentException("Thread amount should be positive.");
        }
        threads = new Thread[threadAmount];
        taskQueue = new LinkedList<>();
        for (int i = 0; i < threadAmount; i++) {
            threads[i] = new Thread(() -> {
                while (!wasShutdown) {
                    ThreadPoolTask currentTask = null;
                    if (!taskQueue.isEmpty()) {
                        synchronized (taskQueue) {
                            if (!taskQueue.isEmpty()) {
                                currentTask = taskQueue.removeFirst();
                            }
                        }
                    }
                    if (currentTask != null) {
                        currentTask.execute();
                    }
                }
            });
            threads[i].start();
        }
    }

    /** Interrupts all threads in pool. */
    public void shutdown() {
        wasShutdown = true;
        for (var thread : threads) {
            thread.interrupt();
        }
    }

    /**
     * Creates a task from the given supplier and adds it to pool queue for processing.
     * @param supplier a supplier for the task creation.
     * @return the created task.
     */
    @NotNull public LightFuture<T> add(@NotNull Supplier<T> supplier) {
        if (wasShutdown) {
            throw new IllegalStateException("Pool was shut down and does not accept new tasks.");
        }
        var task = new ThreadPoolTask(supplier);
        synchronized (taskQueue) {
            taskQueue.add(task);
        }
        return task;
    }

    /** Class that stores tasks for this ThreadPool. */
    private class ThreadPoolTask implements LightFuture<T> {
        private final @NotNull Supplier<T> supplier;
        private boolean ready = false;
        private @Nullable T result = null;
        private @Nullable Exception exception = null;

        private ThreadPoolTask(@NotNull Supplier<T> supplier) {
            this.supplier = supplier;
        }

        /** Returns true if task has been executed or false otherwise. */
        @Override
        public boolean isReady() {
            return ready;
        }

        /**
         * Returns result of the execution of the task.
         * @return result of the execution of the task.
         * @throws LightExecutionException if exception occurred in supplier's calculation.
         */
        @Override
        @Nullable public T get() throws LightExecutionException {
            synchronized (supplier) {
                while (!ready) {
                    try {
                        supplier.wait();
                    } catch (InterruptedException exception) {
                        throw new LightExecutionException("Interrupted calculation: " + exception.getMessage());
                    }
                }
            }
            if (exception != null) {
                throw new LightExecutionException(exception);
            }
            return result;
        }

        /** Executes the task by getting the result from the supplier. */
        private void execute() {
            try {
                result = supplier.get();
            } catch (Exception exception) {
                this.exception = exception;
            }
            ready = true;
            synchronized (supplier) {
                supplier.notifyAll();
            }
        }

        /**
         * Applies the given function to the result of the task.
         * @param function a function to apply to the result of this task.
         * @return a new LightFuture task.
         */
        @Override
        @NotNull public LightFuture<T> thenApply(@NotNull Function<T, T> function) {
            return ThreadPool.this.add(() -> {
                try {
                    return function.apply(ThreadPoolTask.this.get());
                } catch (LightExecutionException exception) {
                    throw new RuntimeException(exception);
                }
            });
        }
    }
}
