package ru.hse.lyubortk.myjunit.exceptions;

public class StaticMethodException extends Exception {
    public StaticMethodException() {
    }

    public StaticMethodException(String message) {
        super (message);
    }

    public StaticMethodException(Throwable cause) {
        super (cause);
    }

    public StaticMethodException(String message, Throwable cause) {
        super (message, cause);
    }
}
