package com.example.quizService.Exception;

import java.util.List;

public class RowValidationException extends RuntimeException {
    private final List<String> errors;

    public RowValidationException(List<String> errors) {
        super("Fel i en eller flera rader");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
