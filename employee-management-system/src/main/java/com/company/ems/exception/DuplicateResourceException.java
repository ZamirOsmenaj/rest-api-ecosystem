package com.company.ems.exception;

/** Thrown when a uniqueness constraint would be violated (e.g. email, department code). Maps to HTTP 409. */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
