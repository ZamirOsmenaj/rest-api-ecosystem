package com.company.ems.exception;

/** Thrown when a requested entity (department, employee, ...) does not exist. Maps to HTTP 404. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
