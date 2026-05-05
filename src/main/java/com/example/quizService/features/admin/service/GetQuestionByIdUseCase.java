package com.example.quizService.features.admin.service;

import com.example.quizService.features.quiz.entity.Question;
import com.example.quizService.features.quiz.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * GetQuestionByIdUseCase - Get specific question by ID
 * 
 * Responsibility:
 * - Fetch single question by ID
 * - Return raw Question entity (not DTO)
 * - Throw exception if not found
 * 
 * Business Rules:
 * - Returns full question data (admin view)
 * - Throws RuntimeException if question not found
 * 
 * Dependencies:
 * - QuestionRepository - fetch by ID
 * 
 * Flow:
 * 1. Find question by ID
 * 2. If found, return entity
 * 3. If not found, throw exception
 * 
 * Used by:
 * - AdminQuizController
 * 
 * Endpoints:
 * - GET /api/admin/quizzes/{id}
 */
@Service
public class GetQuestionByIdUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetQuestionByIdUseCase.class);

    private final QuestionRepository questionRepository;

    public GetQuestionByIdUseCase(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    /**
     * Execute: Get question by ID
     * 
     * @param id Question ID
     * @return Question entity
     * @throws RuntimeException if question not found
     */
    public Question execute(Long id) {
        log.debug("Fetching question id={}", id);

        Question question = questionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Question not found id={}", id);
                    return new RuntimeException("Question not found with ID: " + id);
                });

        log.debug("Found question id={}", id);

        return question;
    }
}
