package com.example.quizService.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "excel_imports")
public class ExcelImportFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private LocalDateTime uploadedAt;

    private boolean dryRun;

    // ✅ KRITISK DEL
    @OneToMany(
        mappedBy = "excelImportFile",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Question> questions;

    public ExcelImportFile() {}

    public Long getId() { return id; }
    public String getFileName() { return fileName; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public boolean isDryRun() { return dryRun; }

    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    public void setDryRun(boolean dryRun) { this.dryRun = dryRun; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
}
