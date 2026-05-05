package com.example.quizService.features.shared.exception;

import java.util.List;

/**
 * RowValidationException - Excel row validation failed
 * 
 * Thrown when:
 * - One or more Excel rows have invalid data
 * - Missing required fields
 * - Invalid data types
 * 
 * Contains list of all row errors for detailed feedback
 */
public class RowValidationException extends RuntimeException {

    private final List<String> errors;

    public RowValidationException(List<String> errors) {
        super("Error in one or more rows");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
