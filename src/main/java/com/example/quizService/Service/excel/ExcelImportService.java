package com.example.quizService.Service.excel;

import com.example.quizService.Entity.Question;
import com.example.quizService.Exception.RowValidationException;
import com.example.quizService.Repository.QuestionRepository;
import com.example.quizService.Util.ExcelValidationUtil;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelImportService implements ExcelImportServiceInterface {

    private final QuestionRepository questionRepository;

    @Autowired
    public ExcelImportService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public int importQuestionsFromExcel(MultipartFile file, boolean dryRun) throws Exception {
        List<Question> questions = new ArrayList<>();
        List<String> allErrors = new ArrayList<>(); 
        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            // Validera header
            ExcelValidationUtil.validateHeader(headerRow);

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                // Samla fel för denna rad 
                List<String> rowErrors = ExcelValidationUtil.validateRow(row, r); 
                if (!rowErrors.isEmpty()) {
                    allErrors.addAll(rowErrors); 
                    continue; // Hoppa mappning om fel finns. 
                }


                // Mappa Question
                Question q = new Question();
                q.setExcelId((int) row.getCell(0).getNumericCellValue());
                q.setQuestion(row.getCell(1).getStringCellValue());
                q.setSfi(row.getCell(2).getStringCellValue());
                q.setCorrectAnswer(row.getCell(3).getStringCellValue());
                q.setWrongAnswer1(row.getCell(4).getStringCellValue());
                q.setWrongAnswer2(row.getCell(5).getStringCellValue());
                q.setWrongAnswer3(row.getCell(6).getStringCellValue());
                q.setExplanationForStudent(row.getCell(7).getStringCellValue());
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
                q.setImage(row.getCell(28).getStringCellValue()); // Valfritt
                q.setSubject((int) row.getCell(29).getNumericCellValue());
                q.setLang(row.getCell(30).getStringCellValue());

                questions.add(q);
            }
            // Om det finns fel kasta exception med alla fel
            if (!allErrors.isEmpty()) {
                throw new RowValidationException(String.join("\n", allErrors));
            }
            if (!dryRun) {
                questionRepository.saveAll(questions);
            }
            return questions.size();
        }
    }
}