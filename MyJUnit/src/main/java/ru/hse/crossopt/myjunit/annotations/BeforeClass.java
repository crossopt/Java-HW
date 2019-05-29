package ru.hse.crossopt.myjunit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Annotation for methods that should be run before the class's tests. */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BeforeClass {
}
