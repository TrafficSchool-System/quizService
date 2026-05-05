package com.example.quizService.features.admin.service;

import com.example.quizService.features.admin.dto.UpdateQuestionDTO;
import com.example.quizService.features.quiz.entity.Question;
import com.example.quizService.features.quiz.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * UpdateQuestionUseCase - Update existing question
 * 
 * Responsibility:
 * - Find question by ID
 * - Update question fields from DTO
 * - Save updated question
 * 
 * Business Rules:
 * - Only updates provided fields
 * - Image field can be filename OR URL (no validation on format)
 * - Throws exception if question not found
 * 
 * Dependencies:
 * - QuestionRepository - find and save question
 * - GetQuestionByIdUseCase - reuse logic for finding question
 * 
 * Flow:
 * 1. Find question by ID (or throw exception)
 * 2. Update all fields from DTO
 * 3. Save updated question
 * 4. Return updated entity
 * 
 * Used by:
 * - AdminQuizController
 * 
 * Endpoints:
 * - PUT /api/admin/quizzes/{id}
 */
@Service
public class UpdateQuestionUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateQuestionUseCase.class);

    private final QuestionRepository questionRepository;
    private final GetQuestionByIdUseCase getQuestionByIdUseCase;

    public UpdateQuestionUseCase(
            QuestionRepository questionRepository,
            GetQuestionByIdUseCase getQuestionByIdUseCase) {
        this.questionRepository = questionRepository;
        this.getQuestionByIdUseCase = getQuestionByIdUseCase;
    }

    /**
     * Execute: Update question
     * 
     * @param id  Question ID
     * @param dto Update data
     * @return Updated question entity
     * @throws RuntimeException if question not found
     */
    public Question execute(Long id, UpdateQuestionDTO dto) {
        log.info("Updating question id={}", id);

        // Step 1: Find existing question
        Question question = getQuestionByIdUseCase.execute(id);

        // Step 2: Update fields
        question.setQuestion(dto.getQuestion());
        question.setSfi(dto.getSfi());
        question.setCorrectAnswer(dto.getCorrectAnswer());
        question.setWrongAnswer1(dto.getWrongAnswer1());
        question.setWrongAnswer2(dto.getWrongAnswer2());
        question.setWrongAnswer3(dto.getWrongAnswer3());
        question.setExplanationForStudent(dto.getExplanationForStudent());
        question.setImage(dto.getImage()); // Can be filename OR URL
        question.setSubject(dto.getSubject());
        question.setLang(dto.getLang());

        // Step 3: Save updated question
        Question updated = questionRepository.save(question);

        log.debug("Question updated id={}", id);

        return updated;
    }
}
