package com.example.invoicing.common.exception;

/**
 * Thrown when a request is well-formed but conflicts with a domain rule
 * (e.g. editing a non-DRAFT invoice or reusing an invoice number).
 * Mapped to HTTP 409 Conflict by {@link GlobalExceptionHandler}.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
