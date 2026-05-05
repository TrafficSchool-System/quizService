package com.example.quizService.features.shared.exception;

/**
 * HeaderValidationException - Excel header validation failed
 * 
 * Thrown when:
 * - Excel file has incorrect header row
 * - Missing required columns
 * - Column order doesn't match expected
 */
public class HeaderValidationException extends RuntimeException {

    public HeaderValidationException(String message) {
        super(message);
    }

    public HeaderValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
