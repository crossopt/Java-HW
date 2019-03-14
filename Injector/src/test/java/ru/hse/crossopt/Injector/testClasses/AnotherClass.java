package ru.hse.crossopt.Injector.testClasses;

public class AnotherClass {

    public final BaseClass dependency;

    public AnotherClass(BaseClass dependency) {
        this.dependency = dependency;
    }
}
