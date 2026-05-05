package com.example.quizService.features.quiz.dto;

import java.util.List;

/**
 * FinalExamDTO - Final exam with questions and duration
 * 
 * Purpose:
 * - Wrapper for final exam questions + metadata
 * - Includes duration for exam timer
 * 
 * Business Rules:
 * - Final exam: 70 questions total
 * - Duration: 50 minutes
 * - Questions selected from all 5 subjects
 * 
 * Used by:
 * - GetFinalExamUseCase
 * 
 * Endpoints:
 * - GET /api/quizzes/final-exam
 */
public class FinalExamDTO {

    private List<QuizQuestionDTO> questions;
    private long durationMinutes; // e.g., 50 minutes

    // =========================
    // Constructors
    // =========================

    public FinalExamDTO() {
    }

    public FinalExamDTO(List<QuizQuestionDTO> questions, long durationMinutes) {
        this.questions = questions;
        this.durationMinutes = durationMinutes;
    }

    // =========================
    // Getters / Setters
    // =========================

    public List<QuizQuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuizQuestionDTO> questions) {
        this.questions = questions;
    }

    public long getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(long durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
