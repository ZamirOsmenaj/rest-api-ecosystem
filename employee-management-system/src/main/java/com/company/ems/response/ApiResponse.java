package com.company.ems.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.Instant;

/**
 * Uniform success-response envelope returned by every endpoint, so consumers
 * always see the same top-level shape ({@code success}, {@code message},
 * {@code data}, {@code timestamp}) regardless of which resource they hit.
 * Errors use {@link com.company.ems.exception.ErrorResponse} instead, kept
 * deliberately separate so error payloads can carry validation details
 * without polluting the happy-path contract.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final Instant timestamp;

    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = Instant.now();
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null, data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }
}
