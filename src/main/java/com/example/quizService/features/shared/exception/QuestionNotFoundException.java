package com.example.quizService.features.shared.exception;

/**
 * QuestionNotFoundException - Question not found by ID
 * 
 * Thrown when:
 * - Question with specified ID doesn't exist
 * - Used in GetQuestionByIdUseCase
 * - Used in UpdateQuestionUseCase
 */
public class QuestionNotFoundException extends RuntimeException {
    
    public QuestionNotFoundException(String message) {
        super(message);
    }

    public QuestionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
