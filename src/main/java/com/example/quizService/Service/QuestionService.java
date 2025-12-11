package com.example.quizService.Service;

import com.example.quizService.Dto.FinalExamDTO;
import com.example.quizService.Dto.QuizQuestionDTO;
import com.example.quizService.Entity.Question;
import com.example.quizService.Exception.QuestionNotFoundException;
import com.example.quizService.Exception.SubjectQuestionCountException;
import com.example.quizService.Repository.QuestionRepository;
import com.example.quizService.Util.QuizMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<QuizQuestionDTO> getQuestionsBySubjects(List<Integer> subjects, int limit) {

        if (subjects == null || subjects.isEmpty()) return Collections.emptyList(); 

        int numSubjects = subjects.size();
        int perSubject = limit / numSubjects; // lika många frågor per ämne
        int remainder = limit % numSubjects;  // fördela udda frågor

        List<QuizQuestionDTO> result = new ArrayList<>();

        for (int i = 0; i < subjects.size(); i++) {
        int subjectLimit = perSubject + (i < remainder ? 1 : 0); // fördela resterande frågor

        List<Question> questionsForSubject = questionRepository.findBySubject(subjects.get(i));
        if (questionsForSubject.isEmpty()) continue;

        Collections.shuffle(questionsForSubject);
        List<Question> selected = questionsForSubject.stream()
                .limit(Math.min(subjectLimit, questionsForSubject.size()))
                .collect(Collectors.toList());

        // Bygg DTO via util-klassen
        for (Question q : selected) {
            result.add(QuizMapper.toDTO(q));
        }
    }

    Collections.shuffle(result); // blanda totalt
    return result;
}


    @Override
    public List<Question> getQuestionsByLang(String lang) {
        return questionRepository.findByLang(lang);
    }

    @Override
    public Question getQuestionById(Long id) {
        Optional<Question> question = questionRepository.findById(id);
        return question.orElseThrow(() -> new QuestionNotFoundException("ID: " + id));
    }

    @Override
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    @Override
    public FinalExamDTO getFinalExam() {

    int[] subjectIds = {1, 2, 3, 4, 5};
    int[] subjectLimits = {1, 1, 1, 1, 1};

    List<QuizQuestionDTO> result = new ArrayList<>();

    for (int i = 0; i < subjectIds.length; i++) {

        List<Question> questionsForSubject = questionRepository.findBySubject(subjectIds[i]);

        if (questionsForSubject.size() < subjectLimits[i]) {
            throw new SubjectQuestionCountException(
                "Subject " + subjectIds[i] + " har bara " + questionsForSubject.size()
                + " frågor men kräver minst " + subjectLimits[i]
            );
        }

        Collections.shuffle(questionsForSubject);

        questionsForSubject.stream()
            .limit(subjectLimits[i])
            .map(QuizMapper::toDTO)
            .forEach(result::add);
    }

    Collections.shuffle(result);

    // Använd din nya DTO-constructorn
    return new FinalExamDTO(result, 50); // ← 50 min timer
}


    
}
