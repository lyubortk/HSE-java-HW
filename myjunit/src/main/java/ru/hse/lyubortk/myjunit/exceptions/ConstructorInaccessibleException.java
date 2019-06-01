package ru.hse.lyubortk.myjunit.exceptions;

public class ConstructorInaccessibleException extends Exception {
    public ConstructorInaccessibleException() {
    }

    public ConstructorInaccessibleException(String message) {
        super (message);
    }

    public ConstructorInaccessibleException(Throwable cause) {
        super (cause);
    }

    public ConstructorInaccessibleException(String message, Throwable cause) {
        super (message, cause);
    }
}
