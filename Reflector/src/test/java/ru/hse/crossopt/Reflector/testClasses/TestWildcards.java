package ru.hse.crossopt.Reflector.testClasses;

public class TestWildcards<T> {
    private Class<?> f1;
    public Class<? super T> f2;
    Class<? extends T> f3() throws IllegalAccessError, LayerInstantiationException { return null; }
    Class<? super T> f4(Class<?> c1, Class<? extends T> c2) {return null;}
    void f5(Class<? super T> c) {}
}
