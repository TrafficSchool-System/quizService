package com.example.quizService.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HeaderValidationException.class)
    public ResponseEntity<String> handleHeaderException(HeaderValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Header-fel: " + ex.getMessage());
    }

    @ExceptionHandler(RowValidationException.class)
    public ResponseEntity<String> handleRowException(RowValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Rad-fel: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Fel: " + ex.getMessage());
    }
}
