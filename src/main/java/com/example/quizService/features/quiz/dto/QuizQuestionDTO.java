package com.example.quizService.features.quiz.dto;

import java.util.List;

/**
 * QuizQuestionDTO - Quiz question for frontend display
 * 
 * Purpose:
 * - Transfer quiz questions to frontend
 * - Hide correct answer until user answers (only sends index)
 * - Answers are shuffled for each question
 * 
 * Image Handling:
 * - image field contains full URL to external image
 * - Example: "https://trafikteori.nu/ElevMedeL/GrunD/korfalt1.jpg"
 * - Frontend uses URL directly - no processing needed
 * 
 * Used by:
 * - GetQuestionsBySubjectsUseCase
 * - GetFinalExamUseCase
 * 
 * Endpoints:
 * - GET /api/quizzes/sessions
 * - GET /api/quizzes/final-exam
 */
public class QuizQuestionDTO {

    private Long id;
    private String question;
    private String sfi; // Question in simplified Swedish
    private List<String> answers; // 4 answers in random order
    private int correctAnswerIndex; // Index of correct answer (0-3)
    private String image; // Full URL to external image
    private String explinationForStudent; // Shown after answering

    // =========================
    // Constructors
    // =========================

    public QuizQuestionDTO() {
    }

    public QuizQuestionDTO(
            Long id,
            String question,
            String sfi,
            List<String> answers,
            int correctAnswerIndex,
            String image,
            String explinationForStudent) {
        this.id = id;
        this.question = question;
        this.sfi = sfi;
        this.answers = answers;
        this.correctAnswerIndex = correctAnswerIndex;
        this.image = image;
        this.explinationForStudent = explinationForStudent;
    }

    // =========================
    // Getters / Setters
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getSfi() {
        return sfi;
    }

    public void setSfi(String sfi) {
        this.sfi = sfi;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    public void setCorrectAnswerIndex(int correctAnswerIndex) {
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getExplinationForStudent() {
        return explinationForStudent;
    }

    public void setExplinationForStudent(String explinationForStudent) {
        this.explinationForStudent = explinationForStudent;
    }
}
