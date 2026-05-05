package com.example.quizService.features.shared.exception;

/**
 * SubjectQuestionCountException - Insufficient questions for subject
 * 
 * Thrown when:
 * - Final exam requires more questions than available for a subject
 * - Subject has fewer than 14 questions (final exam requirement)
 * - Used in GetFinalExamUseCase
 */
public class SubjectQuestionCountException extends RuntimeException {

    public SubjectQuestionCountException(String message) {
        super(message);
    }

    public SubjectQuestionCountException(String message, Throwable cause) {
        super(message, cause);
    }
}
