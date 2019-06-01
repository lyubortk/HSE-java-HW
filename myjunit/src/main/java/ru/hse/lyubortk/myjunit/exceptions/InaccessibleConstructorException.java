package ru.hse.lyubortk.myjunit.exceptions;

public class InaccessibleConstructorException extends Exception {
    public InaccessibleConstructorException() {
    }

    public InaccessibleConstructorException(String message) {
        super (message);
    }

    public InaccessibleConstructorException(Throwable cause) {
        super (cause);
    }

    public InaccessibleConstructorException(String message, Throwable cause) {
        super (message, cause);
    }
}
