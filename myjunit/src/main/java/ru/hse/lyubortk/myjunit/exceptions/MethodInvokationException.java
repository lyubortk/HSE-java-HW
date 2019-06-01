package ru.hse.lyubortk.myjunit.exceptions;

public class MethodInvokationException extends Exception {
    public MethodInvokationException() {
    }

    public MethodInvokationException(String message) {
        super (message);
    }

    public MethodInvokationException(Throwable cause) {
        super (cause);
    }

    public MethodInvokationException(String message, Throwable cause) {
        super (message, cause);
    }
}
