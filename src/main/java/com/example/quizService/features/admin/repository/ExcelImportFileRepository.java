package com.example.quizService.features.admin.repository;

import com.example.quizService.features.admin.entity.ExcelImportFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ExcelImportFileRepository - Data access for Excel import tracking
 * 
 * Purpose:
 * - Store Excel import metadata
 * - Track import history
 * - Enable file-based question management
 * 
 * Used by:
 * - ImportQuestionsUseCase (save import record)
 * - GetAllExcelFilesUseCase (list all imports)
 * - DeleteExcelFileUseCase (remove import record)
 */
@Repository
public interface ExcelImportFileRepository extends JpaRepository<ExcelImportFile, Long> {
    // Standard JpaRepository methods:
    // - findAll(): Get all import records
    // - findById(Long id): Get specific import record
    // - save(ExcelImportFile): Save new import record
    // - delete(ExcelImportFile): Remove import record
}
