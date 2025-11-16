package com.example.quizService.Dto;

import java.util.List;

public class QuizQuestionDTO {

    private Long id; 
    private String question; 
    private List<String> answers; 
    private int correctAnswerIndex; 
    private String image; 
    private String explinationForStudent; 

    public QuizQuestionDTO(){}

    public QuizQuestionDTO(Long id, String question, List<String> answers, int correctAnswerIndex, String image,
            String explinationForStudent) {
        this.id = id;
        this.question = question;
        this.answers = answers;
        this.correctAnswerIndex = correctAnswerIndex;
        this.image = image;
        this.explinationForStudent = explinationForStudent;
    }

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
