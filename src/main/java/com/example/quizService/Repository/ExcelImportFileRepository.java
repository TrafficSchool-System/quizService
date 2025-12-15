package com.example.quizService.Repository;

import com.example.quizService.Entity.ExcelImportFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExcelImportFileRepository extends JpaRepository<ExcelImportFile, Long> {
}
