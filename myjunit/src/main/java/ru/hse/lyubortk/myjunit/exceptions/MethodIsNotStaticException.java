package ru.hse.lyubortk.myjunit.exceptions;

/** This exception is thrown if BeforeClass/AfterClass method is not static. */
public class MethodIsNotStaticException extends Exception {
    public MethodIsNotStaticException() {
    }

    public MethodIsNotStaticException(String message) {
        super (message);
    }

    public MethodIsNotStaticException(Throwable cause) {
        super (cause);
    }

    public MethodIsNotStaticException(String message, Throwable cause) {
        super (message, cause);
    }
}
