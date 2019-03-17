package ru.hse.lyubortk.db;

/**
 * This exception is thrown by PhoneBook methods
 * when operation leads to identical records in database.
 * */
public class AmbiguousRecordException extends Exception {
    public AmbiguousRecordException() {
        super();
    }

    public AmbiguousRecordException(String message) {
        super(message);
    }

    public AmbiguousRecordException(String message, Throwable cause) {
        super(message, cause);
    }

    public AmbiguousRecordException(Throwable cause) {
        super(cause);
    }
}