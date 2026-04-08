package com.example.quizService.Controller;

import com.example.quizService.Dto.ExcelFileDTO;
import com.example.quizService.Dto.FinalExamDTO;
import com.example.quizService.Dto.QuizQuestionDTO;
import com.example.quizService.Dto.UpdateQuestionDTO;
import com.example.quizService.Entity.Question;
import com.example.quizService.Service.QuestionServiceInterface;
import com.example.quizService.Service.excel.ExcelImportServiceInterface;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * QUIZ CONTROLLER
 * 
 * RESTful endpoints for user quiz operations.
 * Base path: /api/quizzes
 * 
 * USER OPERATIONS:
 * - GET /quizzes/final-exam : Get final exam questions
 * - GET /quizzes/sessions : Get quiz questions by subjects (filtered)
 * 
 * ADMIN OPERATIONS:
 * - See AdminQuizController for admin quiz management
 * 
 * AUTHENTICATION:
 * - All endpoints require USER role
 * - Uses X-User-Role header from API Gateway
 */
@RestController
@RequestMapping("/api/quizzes")
public class QuestionController {

    private final QuestionServiceInterface questionService;

    public QuestionController(QuestionServiceInterface questionService) {
        this.questionService = questionService;
    }

    /**
     * GET FINAL EXAM QUESTIONS
     * GET /api/quizzes/final-exam
     * 
     * Returns a set of questions for the final exam.
     * Questions are randomly selected from all subjects.
     * 
     * ACCESSIBLE BY:
     * - USER role (via Gateway headers)
     * - INTERNAL_SERVICE role (via X-Internal-API-Key)
     * 
     * @return Final exam with questions
     */
    @GetMapping("/final-exam")
    @PreAuthorize("hasRole('USER') or hasRole('INTERNAL_SERVICE')")
    public ResponseEntity<FinalExamDTO> getFinalExam() {
        return ResponseEntity.ok(questionService.getFinalExam());
    }

    /**
     * GET QUIZ QUESTIONS BY SUBJECTS
     * GET /api/quizzes/sessions
     * 
     * Returns quiz questions filtered by subjects.
     * Used for practice quizzes with specific subject focus.
     * 
     * @param subjects List of subject IDs to include
     * @param limit    Maximum number of questions to return (default: 10)
     * @return List of quiz questions
     */
    @GetMapping("/sessions")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<QuizQuestionDTO>> getQuizSession(
            @RequestParam List<Integer> subjects,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        List<QuizQuestionDTO> dtos = questionService.getQuestionsBySubjects(subjects, limit);
        return ResponseEntity.ok(dtos);
    }

}
