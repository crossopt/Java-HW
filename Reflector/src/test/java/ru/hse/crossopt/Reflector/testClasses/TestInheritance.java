package ru.hse.crossopt.Reflector.testClasses;

import org.junit.jupiter.api.function.Executable;

import java.util.TreeMap;

public class TestInheritance extends TreeMap implements Runnable, Executable {
    public void run() {}
    public void execute() throws Throwable {}
}
