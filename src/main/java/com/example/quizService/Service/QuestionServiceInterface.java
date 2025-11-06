
package com.example.quizService.Service;

import java.util.List;

import com.example.quizService.Entity.Question;

public interface QuestionServiceInterface {

    Question saveQuestion (Question question);
    List<Question> getAllQuestions();
    List<Question> getQuestionsBySubject(int subject);
    List<Question> getQuestionsByLang(String lang);
    Question getQuestionById(Long id);
    void deleteQuestion(Long id);

    
}