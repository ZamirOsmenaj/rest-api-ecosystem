package com.company.ems.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

/**
 * Uniform error envelope returned by {@link GlobalExceptionHandler} for every
 * failure case, so API consumers can reliably parse error responses without
 * branching per exception type.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final boolean success;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final Instant timestamp;
    private final List<FieldValidationError> errors;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class FieldValidationError {
        private final String field;
        private final String message;
    }
}
