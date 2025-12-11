package com.example.quizService.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.quizService.Entity.Question;
import com.example.quizService.Service.QuestionServiceInterface;
import com.example.quizService.Service.excel.ExcelImportServiceInterface;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/admin/quizzes")
public class AdminQuizController {

    
    private final ExcelImportServiceInterface excelImportService;

    private final QuestionServiceInterface questionService;

    public AdminQuizController(ExcelImportServiceInterface excelImportService, QuestionServiceInterface questionService) {
        this.excelImportService = excelImportService; 
        this.questionService = questionService; 
    }

    // === IMPORTERA EXCEL FILEN - ADMIN ONLY ===
    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> importQuestions(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "dryRun", defaultValue = "false") boolean dryRun) {
        try {
            int importedCount = excelImportService.importQuestionsFromExcel(file, dryRun);
            if (dryRun) {
                return ResponseEntity.ok("Dryrun lyckades! Antal giltiga frågor: " + importedCount);
            } else {
                return ResponseEntity.ok("Import lyckades! Antal importerade frågor: " + importedCount);
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Fel vid import: " + "\n" + e.getMessage());
        }
    }

    // === HÄMTA EN FRÅGA PER ID - ADMIN ONLY ===
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Question> getQuestionById(@PathVariable Long id) {
        Question question = questionService.getQuestionById(id);
        return ResponseEntity.ok(question);
    }

    // === HÄMTA ALLA FRÅGE-OBJEKT (EXCELRAD) FRÅN EXCEL FILEN - ADMIN ONLY ===
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Question> getAllQuestions() {
        return questionService.getAllQuestions();
    }

}
