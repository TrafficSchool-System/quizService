package com.example.quizService.features.admin.service;

import com.example.quizService.features.admin.entity.ExcelImportFile;
import com.example.quizService.features.admin.repository.ExcelImportFileRepository;
import com.example.quizService.features.quiz.entity.Question;
import com.example.quizService.features.quiz.repository.QuestionRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * DeleteExcelFileUseCase - Delete Excel import file record
 * 
 * Responsibility:
 * - Find Excel import file by ID
 * - Decouple questions from file (set excelImportFile = null)
 * - Delete import file record
 * - Questions remain in database (only link removed)
 * 
 * Business Rules:
 * - Questions are NOT deleted (only decoupled from file)
 * - If file not found, throw exception
 * - Decoupling prevents FK constraint errors
 * - Questions can be manually deleted later via UpdateQuestionUseCase if needed
 * 
 * Dependencies:
 * - ExcelImportFileRepository - find and delete file
 * - QuestionRepository - find and update questions
 * 
 * Flow:
 * 1. Find import file by ID (throw if not found)
 * 2. Find all questions linked to this file
 * 3. Decouple questions (set excelImportFile = null)
 * 4. Save updated questions
 * 5. Delete import file record
 * 
 * Used by:
 * - AdminQuizController
 * 
 * Endpoints:
 * - DELETE /api/admin/quizzes/files/{fileId}
 */
@Service
public class DeleteExcelFileUseCase {

    private static final Logger log = LoggerFactory.getLogger(DeleteExcelFileUseCase.class);

    private final ExcelImportFileRepository excelImportFileRepository;
    private final QuestionRepository questionRepository;

    public DeleteExcelFileUseCase(
            ExcelImportFileRepository excelImportFileRepository,
            QuestionRepository questionRepository) {
        this.excelImportFileRepository = excelImportFileRepository;
        this.questionRepository = questionRepository;
    }

    /**
     * Execute: Delete Excel import file
     * 
     * @param fileId Import file ID
     * @throws RuntimeException if file not found
     */
    @Transactional
    public void execute(Long fileId) {
        log.info("Deleting import file id={}", fileId);

        // Step 1: Find import file
        ExcelImportFile file = excelImportFileRepository
                .findById(fileId)
                .orElseThrow(() -> {
                    log.warn("Import file not found id={}", fileId);
                    return new RuntimeException("Excel import file not found with ID: " + fileId);
                });

        log.debug("File name={}", file.getFileName());

        // Step 2: Find all questions linked to this file
        List<Question> questions = questionRepository.findByExcelImportFile(file);

        log.debug("Decoupling {} linked questions", questions.size());

        // Step 3: Decouple questions
        for (Question q : questions) {
            q.setExcelImportFile(null);
        }

        // Step 4: Save updated questions
        questionRepository.saveAll(questions);

        // Step 5: Delete import file record
        excelImportFileRepository.delete(file);

        log.debug("Import file id={} deleted", fileId);
    }
}
