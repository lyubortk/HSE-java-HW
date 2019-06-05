package ru.hse.lyubortk.myjunit.exceptions;

/** This exception is thrown if a constructor of testing class throws an exception */
public class ConstructorInvocationException extends Exception {
    public ConstructorInvocationException() {
    }

    public ConstructorInvocationException(String message) {
        super (message);
    }

    public ConstructorInvocationException(Throwable cause) {
        super (cause);
    }

    public ConstructorInvocationException(String message, Throwable cause) {
        super (message, cause);
    }
}
