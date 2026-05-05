package com.example.quizService.features.admin.dto;

/**
 * UpdateQuestionDTO - Request DTO for updating question
 * 
 * Purpose:
 * - Transfer updated question data from admin frontend
 * - Validate question updates
 * 
 * Image Handling:
 * - image field contains full URL to external image
 * - Example: "https://trafikteori.nu/ElevMedeL/GrunD/korfalt1.jpg"
 * 
 * Used by:
 * - UpdateQuestionUseCase
 * 
 * Endpoints:
 * - PUT /api/admin/quizzes/{id}
 */
public class UpdateQuestionDTO {

    private String question;
    private String sfi;
    private String correctAnswer;
    private String wrongAnswer1;
    private String wrongAnswer2;
    private String wrongAnswer3;
    private String explanationForStudent;
    private String image; // Full URL to external image
    private int subject;
    private String lang;

    // =========================
    // Constructors
    // =========================

    public UpdateQuestionDTO() {
    }

    // =========================
    // Getters / Setters
    // =========================

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

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getWrongAnswer1() {
        return wrongAnswer1;
    }

    public void setWrongAnswer1(String wrongAnswer1) {
        this.wrongAnswer1 = wrongAnswer1;
    }

    public String getWrongAnswer2() {
        return wrongAnswer2;
    }

    public void setWrongAnswer2(String wrongAnswer2) {
        this.wrongAnswer2 = wrongAnswer2;
    }

    public String getWrongAnswer3() {
        return wrongAnswer3;
    }

    public void setWrongAnswer3(String wrongAnswer3) {
        this.wrongAnswer3 = wrongAnswer3;
    }

    public String getExplanationForStudent() {
        return explanationForStudent;
    }

    public void setExplanationForStudent(String explanationForStudent) {
        this.explanationForStudent = explanationForStudent;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getSubject() {
        return subject;
    }

    public void setSubject(int subject) {
        this.subject = subject;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
