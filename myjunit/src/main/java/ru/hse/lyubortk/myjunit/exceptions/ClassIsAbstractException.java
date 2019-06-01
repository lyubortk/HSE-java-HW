package ru.hse.lyubortk.myjunit.exceptions;

/** This exception is thrown if the testing class is abstract but has to be instantiated */
public class ClassIsAbstractException extends Exception {
    public ClassIsAbstractException() {
    }

    public ClassIsAbstractException(String message) {
        super (message);
    }

    public ClassIsAbstractException(Throwable cause) {
        super (cause);
    }

    public ClassIsAbstractException(String message, Throwable cause) {
        super (message, cause);
    }
}
