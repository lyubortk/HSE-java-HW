package ru.hse.lyubortk.myjunit.exceptions;

/**
 * This exception is thrown if a method annotated with After/Before or AfterClass/BeforeClass
 * throws an exception.
 */
public class MethodInvocationException extends Exception {
    public MethodInvocationException() {
    }

    public MethodInvocationException(String message) {
        super (message);
    }

    public MethodInvocationException(Throwable cause) {
        super (cause);
    }

    public MethodInvocationException(String message, Throwable cause) {
        super (message, cause);
    }
}
