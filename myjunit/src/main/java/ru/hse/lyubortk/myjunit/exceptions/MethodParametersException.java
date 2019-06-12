package ru.hse.lyubortk.myjunit.exceptions;

/** This exception is thrown if any method that would be normally invoked by
 * {@link ru.hse.lyubortk.myjunit.MyJUnitCore} has more than 0 parameters.
 */
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
