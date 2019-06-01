package ru.hse.lyubortk.myjunit.exceptions;

public class AbstractClassException extends Exception {
    public AbstractClassException() {
    }

    public AbstractClassException(String message) {
        super (message);
    }

    public AbstractClassException(Throwable cause) {
        super (cause);
    }

    public AbstractClassException(String message, Throwable cause) {
        super (message, cause);
    }
}
