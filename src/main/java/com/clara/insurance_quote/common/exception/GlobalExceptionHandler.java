package com.clara.insurance_quote.common.exception;

import com.clara.insurance_quote.common.dto.ApiErrorDto;
import com.clara.insurance_quote.quoting.exception.IneligibleQuotingDataException;
import com.clara.insurance_quote.quoting.exception.QuoteSubmissionFailedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles age-gating rule violations (e.g., applicant <= 65 submitting health data).
     */
    @ExceptionHandler(IneligibleQuotingDataException.class)
    public ResponseEntity<ApiErrorDto> handleIneligibleUnderwritingData(
            IneligibleQuotingDataException ex,
            HttpServletRequest request) {
        log.warn("Ineligible underwriting data request at {}: {}", request.getRequestURI(), ex.getMessage());

        ApiErrorDto errorDto = ApiErrorDto.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    /**
     * Handles external submission failures (httpstat.us provider failure or timeout).
     */
    @ExceptionHandler(QuoteSubmissionFailedException.class)
    public ResponseEntity<ApiErrorDto> handleQuoteSubmissionFailed(
            QuoteSubmissionFailedException ex,
            HttpServletRequest request) {
        log.error("Quote submission failed at {}: {}", request.getRequestURI(), ex.getMessage());

        ApiErrorDto errorDto = ApiErrorDto.of(
                HttpStatus.BAD_GATEWAY.value(),
                HttpStatus.BAD_GATEWAY.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorDto);
    }

    /**
     * Handles invalid state transitions or generic illegal arguments.
     */
    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiErrorDto> handleBadRequestState(
            RuntimeException ex,
            HttpServletRequest request) {
        log.warn("Invalid state/argument at {}: {}", request.getRequestURI(), ex.getMessage());

        ApiErrorDto errorDto = ApiErrorDto.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    /**
     * Handles Spring Bean Validation errors (@Valid on RequestBodies).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        Map<String, String> validationErrors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            validationErrors.put(error.getField(), error.getDefaultMessage());
        }

        ApiErrorDto errorDto = ApiErrorDto.of(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "One or more fields failed validation checks.",
                request.getRequestURI(),
                validationErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    /**
     * Fallback handler for any uncaught system exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        log.error("Unhandled exception at {}", request.getRequestURI(), ex);

        ApiErrorDto errorDto = ApiErrorDto.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "An unexpected error occurred. Please try again later.",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDto);
    }
}