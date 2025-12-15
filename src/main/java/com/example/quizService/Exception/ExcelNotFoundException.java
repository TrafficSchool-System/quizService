package com.example.quizService.Exception;


public class ExcelNotFoundException extends RuntimeException {

    public ExcelNotFoundException (String message) {
        super(message); 
    }

    public ExcelNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
