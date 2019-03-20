package ru.hse.crossopt.Injector;

public class InjectionCycleException extends Exception {
    public InjectionCycleException(String message) {
        super(message);
    }
}
