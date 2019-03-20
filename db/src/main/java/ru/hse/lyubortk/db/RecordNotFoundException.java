package ru.hse.lyubortk.db;

/**
 * This exception is thrown by PhoneBook methods
 * when operation is performed on non-existent record.
 * */
public class RecordNotFoundException extends Exception {
    public RecordNotFoundException() {
        super();
    }

    public RecordNotFoundException(String message) {
        super(message);
    }

    public RecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecordNotFoundException(Throwable cause) {
        super(cause);
    }
}