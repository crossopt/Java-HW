package ru.hse.crossopt.Reflector.testClasses;

import java.util.function.BiPredicate;

public class TestGenerics<T, S> {
    T t;
    final T finalT = null;
    T[] array;
    T f1(T t, T t2) { return null; }
    T[] f2(T t) { return null; }
    <U> U f4() { return null; }
    <U extends T> U f5() { return null; }
    <U extends T, S> U f6() { return null; }
    BiPredicate <T, T> f7() {return null;}
}