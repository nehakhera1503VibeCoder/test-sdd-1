package com.example.library.exception;

/**
 * Maps to HTTP 409 in {@code ApiExceptionHandler}. Used for business-rule
 * conflicts (FR-4): no available copies, or a duplicate active loan.
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
