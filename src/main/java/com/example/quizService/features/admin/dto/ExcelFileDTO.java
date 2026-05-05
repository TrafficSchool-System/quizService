package com.example.quizService.features.admin.dto;

import java.time.LocalDateTime;

/**
 * ExcelFileDTO - Excel import file metadata
 * 
 * Purpose:
 * - Transfer Excel import file information to admin frontend
 * - Show import history
 * 
 * Fields:
 * - id: Unique identifier
 * - fileName: Original Excel filename
 * - uploadedAt: When file was imported
 * - dryRun: Whether import was validation-only
 * 
 * Used by:
 * - GetAllExcelFilesUseCase
 * 
 * Endpoints:
 * - GET /api/admin/quizzes/files
 */
public class ExcelFileDTO {

    private Long id;
    private String fileName;
    private LocalDateTime uploadedAt;
    private boolean dryRun;

    // =========================
    // Constructors
    // =========================

    public ExcelFileDTO(Long id, String fileName, LocalDateTime uploadedAt, boolean dryRun) {
        this.id = id;
        this.fileName = fileName;
        this.uploadedAt = uploadedAt;
        this.dryRun = dryRun;
    }

    // =========================
    // Getters (no setters - immutable DTO)
    // =========================

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public boolean isDryRun() {
        return dryRun;
    }
}
