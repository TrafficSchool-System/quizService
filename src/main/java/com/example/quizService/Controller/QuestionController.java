package com.example.quizService.Controller;

import com.example.quizService.Dto.FinalExamDTO;
import com.example.quizService.Dto.QuizQuestionDTO;
import com.example.quizService.Service.QuestionServiceInterface;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionServiceInterface questionService;

    public QuestionController(QuestionServiceInterface questionService) {
        this.questionService = questionService;
    }


    // === HÄMTA FINAL EXAM - ENDAST FÖR INLOGGADE ANVÄNDARE ===
    @GetMapping("/final-exam")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<FinalExamDTO> getFinalExam() {
        return ResponseEntity.ok(questionService.getFinalExam());
    }

    // === HÄMTA FRÅGOR FRÅN SUBJECT MED LIMIT - ENDAST FÖR INLOGGADE ANVÄNDARE ===
    @GetMapping("/subjects")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<QuizQuestionDTO>> getQuestionsBySubjects(
            @RequestParam List<Integer> subjects,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        List<QuizQuestionDTO> dtos = questionService.getQuestionsBySubjects(subjects, limit);
        return ResponseEntity.ok(dtos);
    }

    
}
