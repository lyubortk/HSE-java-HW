package ru.hse.lyubortk.myjunit.exceptions;

/** This exception is thrown if a constructor of testing class throws an exception */
public class ConstructorInvokationException extends Exception {
    public ConstructorInvokationException() {
    }

    public ConstructorInvokationException(String message) {
        super (message);
    }

    public ConstructorInvokationException(Throwable cause) {
        super (cause);
    }

    public ConstructorInvokationException(String message, Throwable cause) {
        super (message, cause);
    }
}
