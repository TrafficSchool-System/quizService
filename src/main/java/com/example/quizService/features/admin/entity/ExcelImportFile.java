package com.example.quizService.features.admin.entity;

import com.example.quizService.features.quiz.entity.Question;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ExcelImportFile - Tracks Excel file imports
 * 
 * Purpose:
 * - Maintain audit trail of question imports
 * - Allow admin to see which files have been imported
 * - Enable file-based question management (delete all questions from specific
 * file)
 * 
 * Relationships:
 * - OneToMany Question (all questions imported from this file)
 * 
 * Business Rules:
 * - dryRun: if true, import was validation-only (no questions saved)
 * - Questions can be decoupled from file (set excelImportFile = null) before
 * file deletion
 * - File metadata persists even after questions are modified
 */
@Entity
@Table(name = "excel_imports")
public class ExcelImportFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Original filename of uploaded Excel file
     */
    private String fileName;

    /**
     * When the file was uploaded and processed
     */
    private LocalDateTime uploadedAt;

    /**
     * Whether this was a dry-run (validation only, no save)
     */
    private boolean dryRun;

    /**
     * All questions imported from this file
     * Relationship is managed by Question.excelImportFile (mappedBy)
     */
    @OneToMany(mappedBy = "excelImportFile")
    private List<Question> questions;

    // =========================
    // Constructors
    // =========================

    public ExcelImportFile() {
    }

    // =========================
    // Getters / Setters
    // =========================

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
