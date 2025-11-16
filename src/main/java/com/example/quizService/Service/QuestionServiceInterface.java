
package com.example.quizService.Service;

import java.util.List;

import com.example.quizService.Dto.QuizQuestionDTO;
import com.example.quizService.Entity.Question;

public interface QuestionServiceInterface {

    Question saveQuestion (Question question);
    List<Question> getAllQuestions();
    List<QuizQuestionDTO> getQuestionsBySubject(int subject, int limit); 
    List<Question> getQuestionsByLang(String lang);
    Question getQuestionById(Long id);
    void deleteQuestion(Long id);

    
}