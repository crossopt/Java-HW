package ru.hse.crossopt.Reflector.testClasses;

public class AlsoNotSimpleClass<T> {
    private static final int field1 = 5;
    private T field2;

    <U extends T, S> U method2() { return null; }
    Class<?> field3;

    Class<? extends T> f3() throws IllegalAccessError { return null; }
}
