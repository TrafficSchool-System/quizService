package com.example.quizService.features.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler - Centralized exception handling
 * 
 * Purpose:
 * - Handle exceptions across all controllers
 * - Provide consistent error response format
 * - Map exceptions to HTTP status codes
 * 
 * Exception Mapping:
 * - HeaderValidationException → 400 Bad Request
 * - RowValidationException → 400 Bad Request (with error list)
 * - SubjectQuestionCountException → 400 Bad Request
 * - QuestionNotFoundException → 404 Not Found
 * - ExcelNotFoundException → 404 Not Found
 * - Exception (generic) → 500 Internal Server Error
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Build standard error response body
     */
    private Map<String, Object> buildError(String error, String message, int status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);
        return body;
    }

    /**
     * Handle Excel header validation errors
     */
    @ExceptionHandler(HeaderValidationException.class)
    public ResponseEntity<Map<String, Object>> handleHeaderException(HeaderValidationException ex) {
        return new ResponseEntity<>(
                buildError("Header Error", ex.getMessage(), HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle Excel row validation errors
     * Includes list of all row errors
     */
    @ExceptionHandler(RowValidationException.class)
    public ResponseEntity<Map<String, Object>> handleRowException(RowValidationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Row Error");
        body.put("message", "Error in one or more rows");
        body.put("errors", ex.getErrors()); // Include all row errors

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle insufficient questions for final exam
     */
    @ExceptionHandler(SubjectQuestionCountException.class)
    public ResponseEntity<Map<String, Object>> handleSubjectCountException(SubjectQuestionCountException ex) {
        return new ResponseEntity<>(
                buildError("Subject Question Count Error", ex.getMessage(), HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle question not found errors
     */
    @ExceptionHandler(QuestionNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleQuestionNotFoundException(QuestionNotFoundException ex) {
        return new ResponseEntity<>(
                buildError("Question not found", ex.getMessage(), HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND);
    }

    /**
     * Handle Excel file not found errors
     */
    @ExceptionHandler(ExcelNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleExcelNotFoundException(ExcelNotFoundException ex) {
        return new ResponseEntity<>(
                buildError("Not Found", "Excel file not found.", HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOtherException(Exception ex) {
        // Let Spring Security handle AccessDeniedException
        if (ex instanceof org.springframework.security.access.AccessDeniedException) {
            throw (org.springframework.security.access.AccessDeniedException) ex;
        }

        return new ResponseEntity<>(
                buildError("Internal Server Error", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
