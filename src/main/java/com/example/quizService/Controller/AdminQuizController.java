package com.example.quizService.Controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.quizService.Dto.ExcelFileDTO;
import com.example.quizService.Dto.UpdateQuestionDTO;
import com.example.quizService.Entity.Question;
import com.example.quizService.Repository.ExcelImportFileRepository;
import com.example.quizService.Service.QuestionServiceInterface;
import com.example.quizService.Service.excel.ExcelImportServiceInterface;

import jakarta.validation.Valid;


@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/admin/quizzes")
public class AdminQuizController {

    
    private final ExcelImportServiceInterface excelImportService;

    private final QuestionServiceInterface questionService;

    private final ExcelImportFileRepository excelImportFileRepository;

    public AdminQuizController(ExcelImportServiceInterface excelImportService, QuestionServiceInterface questionService, ExcelImportFileRepository excelImportFileRepository) {
        this.excelImportService = excelImportService; 
        this.questionService = questionService;
        this.excelImportFileRepository = excelImportFileRepository;  
    }

    // === IMPORTERA EXCEL FILEN - ADMIN ONLY ===
    @PostMapping(value = "/import", 
                consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> importQuestions(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "dryRun", defaultValue = "false") boolean dryRun) {

        int importedCount = excelImportService.importQuestionsFromExcel(file, dryRun);

        String message; 
        if (dryRun) {
            message = ("Dryrun lyckades! Antal giltiga frågor: " + importedCount);
        } else {
            message = ("Import lyckades! Antal importerade frågor: " + importedCount);
        }

        return ResponseEntity.ok(message); 
    }

    // === HÄMTA EXCEL FILEN - ADMIN ONLY ===
    @GetMapping("/files")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ExcelFileDTO>> getAllExcelFiles() {
        List<ExcelFileDTO> files = excelImportService.getAllExcelFiles(); 
        return ResponseEntity.ok(files);  

    }

    // === TA BORT EXCEL FILEN - ADMIN ONLY ===
    @DeleteMapping("/files/{fileId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteExcelFile (@PathVariable Long fileId) {
        excelImportService.deleteExcelFile(fileId);
        return ResponseEntity.ok().build();
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

    // === UPPDATERA EN FRÅGA - ADMIN ONLY ===
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Question> updateQuestion(@PathVariable Long id, @Valid @RequestBody UpdateQuestionDTO dto) {
        Question updatedQuestion = questionService.updateQuestion(id, dto);
        return ResponseEntity.ok(updatedQuestion);
    }

}
