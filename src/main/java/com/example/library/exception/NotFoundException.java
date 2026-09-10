package com.example.library.exception;

/** Maps to HTTP 404 in {@code ApiExceptionHandler}. */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
