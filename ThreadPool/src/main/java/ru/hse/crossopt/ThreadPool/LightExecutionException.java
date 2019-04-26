package ru.hse.crossopt.ThreadPool;

/** Exception that is thrown when exception in supplier's calculation occurs. */
public class LightExecutionException extends Exception {
    /** Constructor that creates LightExecutionException from the exception of the supplier. */
    public LightExecutionException(Exception exception) {
        super(exception);
    }
}
