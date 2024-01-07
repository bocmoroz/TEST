package org.warehouse.app.exception;

public class DeletionValidationException extends RuntimeException {

    public DeletionValidationException(String message) {
        super(message);
    }
}
