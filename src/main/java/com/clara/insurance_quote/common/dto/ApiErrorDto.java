package com.clara.insurance_quote.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorDto(
        int status,
        String error,
        String message,
        String path,
        OffsetDateTime timestamp,
        Map<String, String> validationErrors
) {
    public static ApiErrorDto of(int status, String error, String message, String path) {
        return new ApiErrorDto(status, error, message, path, OffsetDateTime.now(), null);
    }

    public static ApiErrorDto of(int status, String error, String message, String path, Map<String, String> validationErrors) {
        return new ApiErrorDto(status, error, message, path, OffsetDateTime.now(), validationErrors);
    }
}
