package com.company.ems.exception;

/**
 * Thrown when an operation is structurally valid but violates a domain/business
 * rule (e.g. deleting a department that still has employees assigned). Maps to HTTP 409.
 */
public class BusinessRuleViolationException extends RuntimeException {
    public BusinessRuleViolationException(String message) {
        super(message);
    }
}
