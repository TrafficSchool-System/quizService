package com.example.quizService.Dto;

import java.time.LocalDateTime;

public class ExcelFileDTO {
    private Long id;
    private String fileName;
    private LocalDateTime uploadedAt;
    private boolean dryRun;

    public ExcelFileDTO(Long id, String fileName, LocalDateTime uploadedAt, boolean dryRun) {
        this.id = id;
        this.fileName = fileName;
        this.uploadedAt = uploadedAt;
        this.dryRun = dryRun;
    }

    public Long getId() { return id; }
    public String getFileName() { return fileName; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public boolean isDryRun() { return dryRun; }
}
