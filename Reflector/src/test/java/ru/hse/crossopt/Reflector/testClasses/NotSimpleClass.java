package ru.hse.crossopt.Reflector.testClasses;

import java.io.Serializable;

public class NotSimpleClass<T> implements Serializable {
    private static final int field1 = 5;

    <U extends T, S extends Object> U method2() { return null; }
    private T field2;
    Class<?> field3;

    Class<? extends T> method1() throws IllegalAccessError, LayerInstantiationException { return null; }
}