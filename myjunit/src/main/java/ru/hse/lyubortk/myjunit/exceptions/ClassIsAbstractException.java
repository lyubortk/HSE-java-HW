package ru.hse.lyubortk.myjunit.exceptions;

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
