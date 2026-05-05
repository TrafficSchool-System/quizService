package com.example.quizService.features.admin.service;

import com.example.quizService.features.admin.dto.ExcelFileDTO;
import com.example.quizService.features.admin.entity.ExcelImportFile;
import com.example.quizService.features.admin.repository.ExcelImportFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * GetAllExcelFilesUseCase - Get all Excel import files
 * 
 * Responsibility:
 * - Fetch all ExcelImportFile records
 * - Convert to DTO for frontend display
 * - Show import history to admin
 * 
 * Business Rules:
 * - Returns all import records (no filtering)
 * - Includes dry-run imports (validation-only)
 * - Sorted by ID (implicitly by database)
 * 
 * Dependencies:
 * - ExcelImportFileRepository - fetch all records
 * 
 * Flow:
 * 1. Fetch all import records
 * 2. Convert to DTO
 * 3. Return list
 * 
 * Used by:
 * - AdminQuizController
 * 
 * Endpoints:
 * - GET /api/admin/quizzes/files
 */
@Service
public class GetAllExcelFilesUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetAllExcelFilesUseCase.class);

    private final ExcelImportFileRepository excelImportFileRepository;

    public GetAllExcelFilesUseCase(ExcelImportFileRepository excelImportFileRepository) {
        this.excelImportFileRepository = excelImportFileRepository;
    }

    /**
     * Execute: Get all Excel import files
     * 
     * @return List of Excel import file metadata
     */
    public List<ExcelFileDTO> execute() {
        log.debug("Fetching all import files");

        List<ExcelImportFile> files = excelImportFileRepository.findAll();

        List<ExcelFileDTO> dtos = files.stream()
                .map(f -> new ExcelFileDTO(
                        f.getId(),
                        f.getFileName(),
                        f.getUploadedAt(),
                        f.isDryRun()))
                .toList();

        log.debug("Returning {} import files", dtos.size());

        return dtos;
    }
}
