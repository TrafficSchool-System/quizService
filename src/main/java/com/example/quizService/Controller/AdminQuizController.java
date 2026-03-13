package com.example.quizService.Controller;

import com.example.quizService.Dto.ExcelFileDTO;
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
 * ADMIN QUIZ CONTROLLER
 * 
 * RESTful endpoints for admin quiz management operations.
 * Base path: /api/admin/quizzes
 * 
 * ADMIN OPERATIONS:
 * - GET /admin/quizzes : List all questions
 * - GET /admin/quizzes/{id} : View specific question
 * - PUT /admin/quizzes/{id} : Update question
 * - POST /admin/quizzes/imports : Import questions from Excel
 * - GET /admin/quizzes/files : List imported Excel files
 * - DELETE /admin/quizzes/files/{id} : Delete Excel file
 * 
 * AUTHENTICATION:
 * - All endpoints require ADMIN role
 * - Uses X-User-Role header from API Gateway
 * 
 * DESIGN PATTERN:
 * - Admin endpoints separated from user endpoints
 * - Focus on question management and bulk operations
 * - Used by admin dashboard for content management
 */
@RestController
@RequestMapping("/api/admin/quizzes")
@PreAuthorize("hasRole('ADMIN')")
public class AdminQuizController {

    private final QuestionServiceInterface questionService;
    private final ExcelImportServiceInterface excelImportService;

    public AdminQuizController(QuestionServiceInterface questionService,
            ExcelImportServiceInterface excelImportService) {
        this.questionService = questionService;
        this.excelImportService = excelImportService;
    }

    /**
     * LIST ALL QUESTIONS
     * GET /api/admin/quizzes
     * 
     * Returns all quiz questions in the system.
     * Used by admin dashboard for question management.
     * 
     * @return List of all questions
     */
    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    /**
     * GET QUESTION BY ID
     * GET /api/admin/quizzes/{id}
     * 
     * Returns specific question details for editing.
     * 
     * @param id Question ID to retrieve
     * @return Question details
     */
    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    /**
     * UPDATE QUESTION
     * PUT /api/admin/quizzes/{id}
     * 
     * Updates question content, answers, or metadata.
     * 
     * @param id  Question ID to update
     * @param dto Updated question data
     * @return Updated question
     */
    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody UpdateQuestionDTO dto) {
        return ResponseEntity.ok(questionService.updateQuestion(id, dto));
    }

    /**
     * IMPORT QUESTIONS FROM EXCEL
     * POST /api/admin/quizzes/imports
     * 
     * Bulk import questions from Excel file.
     * Supports dry-run mode to preview import without saving.
     * 
     * @param file   Excel file containing questions
     * @param dryRun If true, validate without saving (default: false)
     * @return Import result message with count
     */
    @PostMapping(value = "/imports", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importQuestions(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "dryRun", defaultValue = "false") boolean dryRun) {
        int count = excelImportService.importQuestionsFromExcel(file, dryRun);
        String message = dryRun
                ? String.format("Dry run: %d questions would be imported", count)
                : String.format("✅ %d questions imported successfully", count);
        return ResponseEntity.ok(message);
    }

    /**
     * LIST IMPORTED EXCEL FILES
     * GET /api/admin/quizzes/files
     * 
     * Returns list of all Excel files that have been imported.
     * Used to track import history and manage files.
     * 
     * @return List of Excel file metadata
     */
    @GetMapping("/files")
    public ResponseEntity<List<ExcelFileDTO>> getAllExcelFiles() {
        return ResponseEntity.ok(excelImportService.getAllExcelFiles());
    }

    /**
     * DELETE EXCEL FILE
     * DELETE /api/admin/quizzes/files/{fileId}
     * 
     * Removes Excel file record from system.
     * Note: This does not delete questions imported from the file.
     * 
     * @param fileId Excel file ID to delete
     * @return Empty response with 200 OK
     */
    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<Void> deleteExcelFile(@PathVariable Long fileId) {
        excelImportService.deleteExcelFile(fileId);
        return ResponseEntity.ok().build();
    }
}
