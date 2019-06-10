package ru.hse.crossopt.myjunit;

/** Exception that is thrown when TestRunner encounters incorrect annotations. */
class MyJUnitException extends Exception {
    /** Constructor that creates exception for TestRunner. */
    public MyJUnitException(String s) {
        super(s);
    }
}