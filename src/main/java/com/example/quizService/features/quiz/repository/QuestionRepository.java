package com.example.quizService.features.quiz.repository;

import com.example.quizService.features.admin.entity.ExcelImportFile;
import com.example.quizService.features.quiz.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * QuestionRepository - Data access for quiz questions
 * 
 * Purpose:
 * - Find questions by subject (for quiz sessions)
 * - Find questions by language
 * - Check for duplicate questions
 * - Find questions by Excel import file
 * 
 * Custom Queries:
 * - findBySubject: Get all questions for specific subject (used in random
 * selection)
 * - findByLang: Get questions in specific language
 * - existsByQuestionAndCorrectAnswerAndSubjectAndLang: Duplicate detection
 * - findByExcelImportFile: Get all questions from specific Excel import
 */
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * Find all questions for a specific subject
     * 
     * Used by:
     * - GetQuestionsBySubjectsUseCase (quiz session generation)
     * - GetFinalExamUseCase (exam question selection)
     * 
     * @param subject Subject ID (1-5)
     * @return List of questions for that subject
     */
    List<Question> findBySubject(int subject);

    /**
     * Find all questions in a specific language
     * 
     * @param lang Language code (e.g., "sv", "en")
     * @return List of questions in that language
     */
    List<Question> findByLang(String lang);

    /**
     * Find questions by Excel row ID
     * 
     * @param excelId Excel row ID (not primary key)
     * @return List of questions with that Excel ID
     */
    List<Question> findByExcelId(Integer excelId);

    /**
     * Find all questions imported from specific Excel file
     * 
     * Used by:
     * - DeleteExcelFileUseCase (decouple questions before file deletion)
     * 
     * @param excelImportFile The Excel import file entity
     * @return List of questions from that file
     */
    List<Question> findByExcelImportFile(ExcelImportFile excelImportFile);

    /**
     * Check if a question already exists (duplicate detection)
     * 
     * Used by:
     * - ImportQuestionsUseCase (skip duplicates during import)
     * 
     * Uniqueness criteria:
     * - Same question text
     * - Same correct answer
     * - Same subject
     * - Same language
     * 
     * @return true if question exists, false otherwise
     */
    boolean existsByQuestionAndCorrectAnswerAndSubjectAndLang(
            String question,
            String correctAnswer,
            int subject,
            String lang);
}
