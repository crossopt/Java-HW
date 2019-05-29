package ru.hse.crossopt.myjunit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for test methods.
 * Test is not run if ignore argument is set.
 * Expected exception type can be set by expected argument.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
    /** Default reason for ignore method. */
    String EMPTY_REASON = "";

    /**  Returns a throwable which is expected to be thrown from the test. */
    Class<? extends Throwable> expected() default DefaultException.class;

    /** Returns the reason for the test to be ignored. */
    String ignore() default EMPTY_REASON;

    /** Default exception for expected method. */
    class DefaultException extends Throwable {
    }
}
