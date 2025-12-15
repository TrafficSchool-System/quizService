package com.example.quizService.Service.excel;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.quizService.Dto.ExcelFileDTO;

public interface ExcelImportServiceInterface {

    
    int importQuestionsFromExcel(MultipartFile file, boolean dryRun);

    // Ta bort excelfil
    void deleteExcelFile (Long fileId); 

    // Ny metod för filhistorik
    List<ExcelFileDTO> getAllExcelFiles();

}
