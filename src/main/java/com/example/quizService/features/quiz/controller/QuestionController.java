package com.example.quizService.features.quiz.controller;

import com.example.quizService.features.quiz.dto.FinalExamDTO;
import com.example.quizService.features.quiz.dto.QuizQuestionDTO;
import com.example.quizService.features.quiz.service.GetFinalExamUseCase;
import com.example.quizService.features.quiz.service.GetQuestionsBySubjectsUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * QuestionController - User quiz endpoints
 * 
 * Purpose:
 * - Serve quiz questions to users
 * - Provide final exam questions
 * - Support practice quiz sessions
 * 
 * Base path: /api/quizzes
 * 
 * Endpoints:
 * - GET /final-exam : Get final exam (70 questions, 50 minutes)
 * - GET /sessions : Get practice quiz by subjects
 * 
 * Authentication:
 * - USER role (via X-User-Role from API Gateway)
 * - INTERNAL_SERVICE role (for ExamService to call /final-exam)
 * 
 * Design Pattern:
 * - Thin controller (delegates to Use Cases)
 * - Clean Architecture
 * - Single responsibility
 */
@RestController
@RequestMapping("/api/quizzes")
public class QuestionController {

    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    private final GetFinalExamUseCase getFinalExamUseCase;
    private final GetQuestionsBySubjectsUseCase getQuestionsBySubjectsUseCase;

    public QuestionController(
            GetFinalExamUseCase getFinalExamUseCase,
            GetQuestionsBySubjectsUseCase getQuestionsBySubjectsUseCase) {
        this.getFinalExamUseCase = getFinalExamUseCase;
        this.getQuestionsBySubjectsUseCase = getQuestionsBySubjectsUseCase;
    }

    /**
     * GET FINAL EXAM QUESTIONS
     * GET /api/quizzes/final-exam
     * 
     * Returns final exam with 70 questions (14 per subject from subjects 1-5).
     * Used by ExamService to create exam sessions.
     * 
     * Business Rules:
     * - 70 questions total
     * - 50 minutes duration
     * - Questions shuffled
     * 
     * Accessible by:
     * - USER role (authenticated users)
     * - INTERNAL_SERVICE role (ExamService via X-Internal-API-Key)
     * 
     * @return Final exam with questions and duration
     */
    @GetMapping("/final-exam")
    @PreAuthorize("hasRole('USER') or hasRole('INTERNAL_SERVICE')")
    public ResponseEntity<FinalExamDTO> getFinalExam() {
        log.debug("GET /api/quizzes/final-exam");
        FinalExamDTO exam = getFinalExamUseCase.execute();
        return ResponseEntity.ok(exam);
    }

    /**
     * GET QUIZ QUESTIONS BY SUBJECTS
     * GET /api/quizzes/sessions
     * 
     * Returns practice quiz questions filtered by subjects.
     * Questions distributed evenly across selected subjects.
     * 
     * Business Rules:
     * - Questions shuffled per subject
     * - Final shuffle across all subjects
     * - Default limit: 10 questions
     * 
     * Example:
     * - GET /api/quizzes/sessions?subjects=1,2,3&limit=9
     * - Returns 9 questions: 3 from each subject
     * 
     * @param subjects List of subject IDs to include
     * @param limit    Total number of questions (default: 10)
     * @return List of quiz questions
     */
    @GetMapping("/sessions")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<QuizQuestionDTO>> getQuizSession(
            @RequestParam List<Integer> subjects,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        log.debug("GET /api/quizzes/sessions subjects={} limit={}", subjects, limit);

        List<QuizQuestionDTO> questions = getQuestionsBySubjectsUseCase.execute(subjects, limit);
        return ResponseEntity.ok(questions);
    }
}
