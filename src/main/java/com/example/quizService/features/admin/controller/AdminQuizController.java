package com.example.quizService.features.admin.controller;

import com.example.quizService.features.admin.dto.ExcelFileDTO;
import com.example.quizService.features.admin.dto.UpdateQuestionDTO;
import com.example.quizService.features.admin.service.*;
import com.example.quizService.features.quiz.entity.Question;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * AdminQuizController - Admin quiz management endpoints
 * 
 * Purpose:
 * - Manage quiz questions (CRUD operations)
 * - Import questions from Excel files
 * - Track Excel import history
 * 
 * Base path: /api/admin/quizzes
 * 
 * Endpoints:
 * - GET /admin/quizzes : List all questions
 * - GET /admin/quizzes/{id} : Get specific question
 * - PUT /admin/quizzes/{id} : Update question
 * - POST /admin/quizzes/imports : Import from Excel/ZIP
 * - GET /admin/quizzes/files : List Excel imports
 * - DELETE /admin/quizzes/files/{id} : Delete import record
 * 
 * Authentication:
 * - ADMIN role (via X-User-Role from API Gateway)
 * 
 * Design Pattern:
 * - Thin controller (delegates to Use Cases)
 * - Clean Architecture
 * - Single responsibility
 * - Admin endpoints separated from user endpoints
 */
@RestController
@RequestMapping("/api/admin/quizzes")
@PreAuthorize("hasRole('ADMIN')")
public class AdminQuizController {

    private static final Logger log = LoggerFactory.getLogger(AdminQuizController.class);

    private final GetAllQuestionsUseCase getAllQuestionsUseCase;
    private final GetQuestionByIdUseCase getQuestionByIdUseCase;
    private final UpdateQuestionUseCase updateQuestionUseCase;
    private final ImportQuestionsUseCase importQuestionsUseCase;
    private final GetAllExcelFilesUseCase getAllExcelFilesUseCase;
    private final DeleteExcelFileUseCase deleteExcelFileUseCase;

    public AdminQuizController(
            GetAllQuestionsUseCase getAllQuestionsUseCase,
            GetQuestionByIdUseCase getQuestionByIdUseCase,
            UpdateQuestionUseCase updateQuestionUseCase,
            ImportQuestionsUseCase importQuestionsUseCase,
            GetAllExcelFilesUseCase getAllExcelFilesUseCase,
            DeleteExcelFileUseCase deleteExcelFileUseCase) {
        this.getAllQuestionsUseCase = getAllQuestionsUseCase;
        this.getQuestionByIdUseCase = getQuestionByIdUseCase;
        this.updateQuestionUseCase = updateQuestionUseCase;
        this.importQuestionsUseCase = importQuestionsUseCase;
        this.getAllExcelFilesUseCase = getAllExcelFilesUseCase;
        this.deleteExcelFileUseCase = deleteExcelFileUseCase;
    }

    /**
     * LIST ALL QUESTIONS
     * GET /api/admin/quizzes
     * 
     * Returns all quiz questions for admin management.
     * Used by admin dashboard to display question list.
     * 
     * @return List of all questions
     */
    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions() {
        log.debug("GET /api/admin/quizzes");
        List<Question> questions = getAllQuestionsUseCase.execute();
        return ResponseEntity.ok(questions);
    }

    /**
     * GET QUESTION BY ID
     * GET /api/admin/quizzes/{id}
     * 
     * Returns specific question details for editing.
     * 
     * @param id Question ID
     * @return Question entity
     */
    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Long id) {
        log.debug("GET /api/admin/quizzes/{}", id);
        Question question = getQuestionByIdUseCase.execute(id);
        return ResponseEntity.ok(question);
    }

    /**
     * UPDATE QUESTION
     * PUT /api/admin/quizzes/{id}
     * 
     * Updates question content, answers, image, or metadata.
     * 
     * Image field (dto.image):
     * - Full URL to external image
     * - Example: "https://trafikteori.nu/ElevMedeL/GrunD/korfalt1.jpg"
     * 
     * @param id  Question ID
     * @param dto Updated question data
     * @return Updated question
     */
    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody UpdateQuestionDTO dto) {
        log.debug("PUT /api/admin/quizzes/{}", id);
        Question updated = updateQuestionUseCase.execute(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * IMPORT QUESTIONS FROM EXCEL
     * POST /api/admin/quizzes/imports
     * 
     * Bulk import questions from Excel file (.xlsx).
     * 
     * File format:
     * - .xlsx: Excel file with 31 columns
     * - Columns 0-30: Question data, answers, permissions, subject, language
     * - Column 28 (IMAGE): Full URL to external image
     * 
     * Image handling:
     * - Column 28 contains full URLs like:
     * "https://trafikteori.nu/ElevMedeL/GrunD/korfalt1.jpg"
     * - URLs are saved as-is and used directly by frontend
     * - No local image storage required
     * 
     * Validation:
     * - Validates all 31 columns
     * - Skips empty rows at end of file
     * - Checks for duplicates (question + answer + subject + language)
     * 
     * Dry-run mode:
     * - dryRun=true: Validates file without saving
     * - dryRun=false: Saves questions to database
     * 
     * @param file   Excel file (.xlsx)
     * @param dryRun Validation-only mode (default: false)
     * @return Import result message with count
     */
    @PostMapping(value = "/imports", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importQuestions(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "dryRun", defaultValue = "false") boolean dryRun) {
        log.debug("POST /api/admin/quizzes/imports file={} dryRun={}", file.getOriginalFilename(), dryRun);

        int count = importQuestionsUseCase.execute(file, dryRun);

        String message = dryRun
                ? String.format("🔍 Dry run: %d questions would be imported", count)
                : String.format("✅ %d questions imported successfully", count);

        return ResponseEntity.ok(message);
    }

    /**
     * LIST IMPORTED EXCEL FILES
     * GET /api/admin/quizzes/files
     * 
     * Returns list of all Excel imports for audit trail.
     * Shows import history with file names and timestamps.
     * 
     * @return List of Excel import metadata
     */
    @GetMapping("/files")
    public ResponseEntity<List<ExcelFileDTO>> getAllExcelFiles() {
        log.debug("GET /api/admin/quizzes/files");
        List<ExcelFileDTO> files = getAllExcelFilesUseCase.execute();
        return ResponseEntity.ok(files);
    }

    /**
     * DELETE EXCEL FILE
     * DELETE /api/admin/quizzes/files/{fileId}
     * 
     * Removes Excel import record from system.
     * 
     * Business Rules:
     * - Questions imported from this file are NOT deleted
     * - Questions are decoupled (excelImportFile set to null)
     * - Only removes import metadata record
     * 
     * @param fileId Excel import file ID
     * @return Empty response (200 OK)
     */
    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<Void> deleteExcelFile(@PathVariable Long fileId) {
        log.debug("DELETE /api/admin/quizzes/files/{}", fileId);
        deleteExcelFileUseCase.execute(fileId);
        return ResponseEntity.ok().build();
    }
}
