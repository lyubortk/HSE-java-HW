package ru.hse.lyubortk.myjunit.exceptions;

/**
 * This exception is thrown if the testing class has to be instantiated
 * but has no matching constructor
 */
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
