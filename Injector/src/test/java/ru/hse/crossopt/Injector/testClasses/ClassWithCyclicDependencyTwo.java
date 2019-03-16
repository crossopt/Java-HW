package ru.hse.crossopt.Injector.testClasses;

public class ClassWithCyclicDependencyTwo {

    public final ClassWithCyclicDependencyOne dependency;

    public ClassWithCyclicDependencyTwo(ClassWithCyclicDependencyOne dependency) {
        this.dependency = dependency;
    }
}
