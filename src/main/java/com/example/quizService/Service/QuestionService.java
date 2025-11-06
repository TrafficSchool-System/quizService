package com.example.quizService.Service;

import com.example.quizService.Entity.Question;
import com.example.quizService.Repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService implements QuestionServiceInterface {

    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public List<Question> getQuestionsBySubject(int subject) {
        return questionRepository.findBySubject(subject);
    }

    @Override
    public List<Question> getQuestionsByLang(String lang) {
        return questionRepository.findByLang(lang);
    }

    @Override
    public Question getQuestionById(Long id) {
        Optional<Question> question = questionRepository.findById(id);
        return question.orElse(null);
    }

    @Override
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }
}
