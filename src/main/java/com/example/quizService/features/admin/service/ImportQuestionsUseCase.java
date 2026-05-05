package com.example.quizService.features.admin.service;

import com.example.quizService.features.admin.entity.ExcelImportFile;
import com.example.quizService.features.admin.repository.ExcelImportFileRepository;
import com.example.quizService.features.quiz.entity.Question;
import com.example.quizService.features.quiz.repository.QuestionRepository;
import com.example.quizService.features.shared.exception.HeaderValidationException;
import com.example.quizService.features.shared.exception.RowValidationException;
import com.example.quizService.features.shared.util.ExcelValidationUtil;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ImportQuestionsUseCase - Import questions from Excel file
 * 
 * Responsibility:
 * - Handle Excel file upload
 * - Validate Excel headers and rows
 * - Map rows to Question entities
 * - Filter duplicates (against DB and within file)
 * - Save questions and import metadata
 * - Support dry-run mode (validation only)
 * 
 * Business Rules:
 * - Accepts .xlsx files only
 * - Image field (cell 28): Contains full URLs to external images
 * - Duplicate detection: question+correctAnswer+subject+lang (case-insensitive)
 * - Dry-run: validates but doesn't save
 * - All questions linked to ExcelImportFile for audit trail
 * 
 * Dependencies:
 * - QuestionRepository - save questions, check duplicates
 * - ExcelImportFileRepository - save import metadata
 * - ExcelValidationUtil - validate headers and rows
 * 
 * Flow:
 * 1. Validate file is not empty
 * 2. Read Excel workbook
 * 3. Validate header row
 * 4. Read and validate all rows (skip empty rows)
 * 5. Map rows to Question entities
 * 6. Filter duplicates (DB + in-file)
 * 7. If not dry-run:
 * - Create ExcelImportFile record
 * - Link questions to file
 * - Save all questions
 * 8. Return count of imported questions
 * 
 * Used by:
 * - AdminQuizController
 * 
 * Endpoints:
 * - POST /api/admin/quizzes/imports?dryRun=false
 */
@Service
public class ImportQuestionsUseCase {

    private static final Logger log = LoggerFactory.getLogger(ImportQuestionsUseCase.class);

    private final QuestionRepository questionRepository;
    private final ExcelImportFileRepository excelImportFileRepository;

    public ImportQuestionsUseCase(
            QuestionRepository questionRepository,
            ExcelImportFileRepository excelImportFileRepository) {
        this.questionRepository = questionRepository;
        this.excelImportFileRepository = excelImportFileRepository;
    }

    /**
     * Execute: Import questions from Excel file
     * 
     * @param file   Excel file (.xlsx) with question data and image URLs
     * @param dryRun If true, only validates (doesn't save)
     * @return Number of questions imported (or would be imported if dry-run)
     * @throws RuntimeException if file is empty, invalid format, or validation
     *                          fails
     */
    @Transactional
    public int execute(MultipartFile file, boolean dryRun) {
        log.info("Import starting file={} dryRun={}", file.getOriginalFilename(), dryRun);

        // Step 1: Validate file
        if (file.isEmpty()) {
            throw new RuntimeException("No Excel file found to import");
        }

        // Step 2: Validate file type
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || !originalFileName.toLowerCase().endsWith(".xlsx")) {
            throw new RuntimeException("Invalid file type. Only .xlsx files are supported.");
        }

        List<Question> questionsFromExcel = new ArrayList<>();
        List<String> allErrors = new ArrayList<>();

        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            log.debug("Reading sheet={} rows={}", sheet.getSheetName(), sheet.getLastRowNum());

            // Step 3: Validate header
            ExcelValidationUtil.validateHeader(sheet.getRow(0));

            // Step 4: Read and validate rows
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);

                // Skip null or completely empty rows (common at end of Excel files)
                if (row == null || ExcelValidationUtil.isRowEmpty(row)) {
                    continue;
                }

                // Validate row
                List<String> rowErrors = ExcelValidationUtil.validateRow(row, r);
                if (!rowErrors.isEmpty()) {
                    allErrors.addAll(rowErrors);
                    continue;
                }

                // Step 5: Map row to Question
                questionsFromExcel.add(mapRowToQuestion(row));
            }
        } catch (HeaderValidationException | RowValidationException e) {
            log.warn("Validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during import", e);
            throw new RuntimeException("Unexpected error during Excel file import", e);
        }

        // If validation errors, throw
        if (!allErrors.isEmpty()) {
            throw new RowValidationException(allErrors);
        }

        log.debug("Rows read from Excel: {}", questionsFromExcel.size());

        // Step 6: Filter duplicates
        List<Question> filteredQuestions = filterDuplicates(questionsFromExcel);

        log.debug("After deduplication: {}", filteredQuestions.size());

        // Step 7: Save if not dry-run
        if (!dryRun) {
            // Create ExcelImportFile record
            ExcelImportFile excelFileEntity = new ExcelImportFile();
            excelFileEntity.setFileName(file.getOriginalFilename());
            excelFileEntity.setUploadedAt(LocalDateTime.now());
            excelFileEntity.setDryRun(false);
            excelImportFileRepository.save(excelFileEntity);

            log.debug("Created import record id={}", excelFileEntity.getId());

            // Link questions to file
            for (Question q : filteredQuestions) {
                q.setExcelImportFile(excelFileEntity);
            }

            // Save all questions
            questionRepository.saveAll(filteredQuestions);

            log.info("Import complete: {} questions imported", filteredQuestions.size());
        } else {
            log.info("Dry-run: {} questions would be imported", filteredQuestions.size());
        }

        return filteredQuestions.size();
    }

    /**
     * Filter duplicates against DB and within same file
     * 
     * @param questions Questions from Excel
     * @return Filtered list (no duplicates)
     */
    private List<Question> filterDuplicates(List<Question> questions) {
        List<Question> filtered = new ArrayList<>();
        Set<String> seenQuestions = new HashSet<>();

        for (Question q : questions) {
            // Create unique key (case-insensitive)
            String key = (q.getQuestion().trim().toLowerCase() + "|"
                    + q.getCorrectAnswer().trim().toLowerCase() + "|"
                    + q.getSubject() + "|"
                    + q.getLang().trim().toLowerCase());

            // Check DB
            boolean existsInDB = questionRepository.existsByQuestionAndCorrectAnswerAndSubjectAndLang(
                    q.getQuestion(), q.getCorrectAnswer(), q.getSubject(), q.getLang());

            // Only add if not in DB and not seen in this file
            if (!existsInDB && !seenQuestions.contains(key)) {
                filtered.add(q);
                seenQuestions.add(key);
            }
        }

        return filtered;
    }

    /**
     * Map Excel row to Question entity
     * 
     * Cell 28 (image): Contains full URL to external image
     * Example: "https://trafikteori.nu/ElevMedeL/GrunD/korfalt1.jpg"
     * 
     * The URL is read as String from cell 28 and saved as-is.
     * Frontend uses the URL directly - no processing needed.
     * 
     * @param row Excel row
     * @return Question entity
     */
    private Question mapRowToQuestion(Row row) {
        Question q = new Question();
        q.setExcelId((int) row.getCell(0).getNumericCellValue());
        q.setQuestion(row.getCell(1).getStringCellValue());
        q.setSfi(row.getCell(2).getStringCellValue());
        q.setCorrectAnswer(row.getCell(3).getStringCellValue());
        q.setWrongAnswer1(row.getCell(4).getStringCellValue());
        q.setWrongAnswer2(row.getCell(5).getStringCellValue());
        q.setWrongAnswer3(row.getCell(6).getStringCellValue());
        q.setExplanationForStudent(row.getCell(7).getStringCellValue());

        // Driver's license categories (8-27)
        q.setA((int) row.getCell(8).getNumericCellValue());
        q.setAm((int) row.getCell(9).getNumericCellValue());
        q.setB((int) row.getCell(10).getNumericCellValue());
        q.setBe((int) row.getCell(11).getNumericCellValue());
        q.setC((int) row.getCell(12).getNumericCellValue());
        q.setCe((int) row.getCell(13).getNumericCellValue());
        q.setD((int) row.getCell(14).getNumericCellValue());
        q.setDe((int) row.getCell(15).getNumericCellValue());
        q.setYkbC((int) row.getCell(16).getNumericCellValue());
        q.setYkbD((int) row.getCell(17).getNumericCellValue());
        q.setAdr((int) row.getCell(18).getNumericCellValue());
        q.setVtl((int) row.getCell(19).getNumericCellValue());
        q.setTa1i1((int) row.getCell(20).getNumericCellValue());
        q.setTa1i2((int) row.getCell(21).getNumericCellValue());
        q.setTa1i3((int) row.getCell(22).getNumericCellValue());
        q.setTa1i4((int) row.getCell(23).getNumericCellValue());
        q.setTa1i5((int) row.getCell(24).getNumericCellValue());
        q.setApv((int) row.getCell(25).getNumericCellValue());
        q.setYrs((int) row.getCell(26).getNumericCellValue());
        q.setTra1((int) row.getCell(27).getNumericCellValue());

        // Image (cell 28) - can be filename OR URL!
        q.setImage(row.getCell(28).getStringCellValue());

        // Subject and language (29-30)
        q.setSubject((int) row.getCell(29).getNumericCellValue());
        q.setLang(row.getCell(30).getStringCellValue());

        return q;
    }
}
