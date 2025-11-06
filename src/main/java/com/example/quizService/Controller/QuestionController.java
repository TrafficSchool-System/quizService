package com.example.quizService.Controller;

import com.example.quizService.Entity.Question;
import com.example.quizService.Service.QuestionService;
import com.example.quizService.Service.QuestionServiceInterface;
import com.example.quizService.Service.excel.ExcelImportServiceInterface;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final ExcelImportServiceInterface excelImportService;
    private final QuestionServiceInterface questionService; 

    @Autowired
    public QuestionController(ExcelImportServiceInterface excelImportService, QuestionServiceInterface questionService) {
        this.excelImportService = excelImportService;
        this.questionService = questionService;
    }

    //=== HÄMTA ALLA FRÅGE-OBJEKT (EXCELRAD) FRÅN EXCEL FILEN ===
    @GetMapping
    public List<Question> getAllQuestions() {
        return questionService.getAllQuestions(); 
    }

    //=== HÄMTA EN FRÅGA PER ID  ===
    @GetMapping("/{id}")
    public Question getQuestionById(@PathVariable Long id) {
        return questionService.getQuestionById(id);
    }


    //=== IMPORTERA EXCEL FILEN ===
    @PostMapping("/import")
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
}