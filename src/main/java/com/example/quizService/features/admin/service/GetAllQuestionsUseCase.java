package com.example.quizService.features.admin.service;

import com.example.quizService.features.quiz.entity.Question;
import com.example.quizService.features.quiz.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * GetAllQuestionsUseCase - Get all questions (admin view)
 * 
 * Responsibility:
 * - Fetch all questions in database
 * - Return raw Question entities (not DTO)
 * - Used by admin panel for question management
 * 
 * Business Rules:
 * - No filtering, no pagination (returns all)
 * - Includes all fields (unlike quiz DTO which hides some)
 * - Admin-only endpoint
 * 
 * Dependencies:
 * - QuestionRepository - fetch all questions
 * 
 * Flow:
 * 1. Fetch all questions
 * 2. Return list
 * 
 * Used by:
 * - AdminQuizController
 * 
 * Endpoints:
 * - GET /api/admin/quizzes
 */
@Service
public class GetAllQuestionsUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetAllQuestionsUseCase.class);

    private final QuestionRepository questionRepository;

    public GetAllQuestionsUseCase(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    /**
     * Execute: Get all questions
     * 
     * @return List of all questions in database
     */
    public List<Question> execute() {
        log.debug("Fetching all questions");

        List<Question> questions = questionRepository.findAll();

        log.debug("Returning {} questions", questions.size());

        return questions;
    }
}
