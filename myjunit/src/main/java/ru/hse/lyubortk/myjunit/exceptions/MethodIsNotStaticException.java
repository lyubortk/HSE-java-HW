package ru.hse.lyubortk.myjunit.exceptions;

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
