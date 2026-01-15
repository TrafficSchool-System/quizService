
package com.example.quizService.Service;

import java.util.List;

import com.example.quizService.Dto.FinalExamDTO;
import com.example.quizService.Dto.QuizQuestionDTO;
import com.example.quizService.Dto.UpdateQuestionDTO;
import com.example.quizService.Entity.Question;

public interface QuestionServiceInterface {

    Question saveQuestion (Question question);
    List<Question> getAllQuestions();
    List<QuizQuestionDTO> getQuestionsBySubjects(List<Integer> subject, int limit);
    FinalExamDTO getFinalExam(); 
    List<Question> getQuestionsByLang(String lang);
    Question getQuestionById(Long id);
    Question updateQuestion(Long id, UpdateQuestionDTO dto);
    void deleteQuestion(Long id);

    
}