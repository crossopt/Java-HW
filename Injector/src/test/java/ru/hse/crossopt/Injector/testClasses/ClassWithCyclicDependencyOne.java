package ru.hse.crossopt.Injector.testClasses;

public class ClassWithCyclicDependencyOne {

    public final ClassWithCyclicDependencyTwo dependency;

    public ClassWithCyclicDependencyOne(ClassWithCyclicDependencyTwo dependency) {
        this.dependency = dependency;
    }
}
