package com.example.quizService.Exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> buildError(String error, String message, int status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);
        return body;
    }

    @ExceptionHandler(HeaderValidationException.class)
    public ResponseEntity<Map<String, Object>> handleHeaderException(HeaderValidationException ex) {
        return new ResponseEntity<>(
            buildError("Header Error", ex.getMessage(), HttpStatus.BAD_REQUEST.value()),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(RowValidationException.class)
    public ResponseEntity<Map<String, Object>> handleRowException(RowValidationException ex) {
        return new ResponseEntity<>(
            buildError("Row Error", ex.getMessage(), HttpStatus.BAD_REQUEST.value()),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(SubjectQuestionCountException.class)
    public ResponseEntity<Map<String, Object>> handleSubjectCountException(SubjectQuestionCountException ex) {
        return new ResponseEntity<>(
            buildError("Subject Question Count Error", ex.getMessage(), HttpStatus.BAD_REQUEST.value()),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(QuestionNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleQuestionNotFoundException(QuestionNotFoundException ex) {
        return new ResponseEntity<>(
            buildError("Question not found", ex.getMessage(), HttpStatus.NOT_FOUND.value()),
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOtherException(Exception ex) {
        if (ex instanceof org.springframework.security.access.AccessDeniedException) {
            // Låt Spring Security ta hand om detta
            throw (org.springframework.security.access.AccessDeniedException) ex;
        }

        return new ResponseEntity<>(
            buildError("Internal Server Error", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}