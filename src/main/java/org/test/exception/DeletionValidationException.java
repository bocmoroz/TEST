package org.test.exception;

public class DeletionValidationException extends RuntimeException {

    public DeletionValidationException(String message) {
        super(message);
    }
}
