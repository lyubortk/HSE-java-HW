package ru.hse.lyubortk.myjunit.exceptions;

public class MethodParametersException extends Exception {
    public MethodParametersException() {
    }

    public MethodParametersException(String message) {
        super (message);
    }

    public MethodParametersException(Throwable cause) {
        super (cause);
    }

    public MethodParametersException(String message, Throwable cause) {
        super (message, cause);
    }
}
