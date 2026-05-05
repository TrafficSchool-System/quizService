package com.example.quizService.features.shared.exception;

/**
 * ExcelNotFoundException - Excel file not found
 * 
 * Thrown when:
 * - Excel import file not found by ID
 * - Uploaded file is empty
 */
public class ExcelNotFoundException extends RuntimeException {

    public ExcelNotFoundException(String message) {
        super(message);
    }

    public ExcelNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
