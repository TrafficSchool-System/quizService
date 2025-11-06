package com.example.quizService.Service.excel;

import org.springframework.web.multipart.MultipartFile;

public interface ExcelImportServiceInterface {
    int importQuestionsFromExcel(MultipartFile file, boolean dryRun) throws Exception; 

}
