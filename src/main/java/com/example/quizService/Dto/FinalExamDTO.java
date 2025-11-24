package com.example.quizService.Dto;

import java.util.List;

public class FinalExamDTO {

    private List<QuizQuestionDTO> questions;
    private long durationMinutes; // exempel: 50 min

    public FinalExamDTO() {}

    public FinalExamDTO(List<QuizQuestionDTO> questions, long durationMinutes) {
        this.questions = questions;
        this.durationMinutes = durationMinutes;
    }

    public List<QuizQuestionDTO> getQuestions() { return questions; }
    public void setQuestions(List<QuizQuestionDTO> questions) { this.questions = questions; }

    public long getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(long durationMinutes) { this.durationMinutes = durationMinutes; }
}