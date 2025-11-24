package com.example.quizService.Exception;


public class SubjectQuestionCountException extends RuntimeException {

    public SubjectQuestionCountException (String message) {
        super(message); 
    }

    public SubjectQuestionCountException(String message, Throwable cause) {
        super(message, cause);
    }
}
