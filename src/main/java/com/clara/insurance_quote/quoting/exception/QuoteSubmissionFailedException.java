package com.clara.insurance_quote.quoting.exception;

public class QuoteSubmissionFailedException extends RuntimeException {
    public QuoteSubmissionFailedException(String message) {
        super(message);
    }
}