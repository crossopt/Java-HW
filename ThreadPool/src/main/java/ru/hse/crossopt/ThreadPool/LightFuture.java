package ru.hse.crossopt.ThreadPool;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/** Interface that stores the tasks that will be executed in a ThreadPool. */
public interface LightFuture<T> {
    /** Returns true if task has been executed or false otherwise. */
    boolean isReady();

    /**
     * Returns result of the execution of the task.
     * @return result of the execution of the task.
     * @throws LightExecutionException if exception occurred in supplier's calculation.
     */
    @Nullable T get() throws LightExecutionException;

    /**
     * Applies the given function to the result of the task.
     * @param function a function to apply to the result of this task.
     * @param <R> the type of the new task.
     * @return a new LightFuture task.
     */
    @NotNull <R> LightFuture<R> thenApply(@NotNull Function<? super T, R> function);
}
