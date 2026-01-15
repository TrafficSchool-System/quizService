package com.example.quizService.Service.excel;

import com.example.quizService.Dto.ExcelFileDTO;
import com.example.quizService.Entity.ExcelImportFile;
import com.example.quizService.Entity.Question;
import com.example.quizService.Exception.ExcelNotFoundException;
import com.example.quizService.Exception.HeaderValidationException;
import com.example.quizService.Exception.RowValidationException;
import com.example.quizService.Repository.ExcelImportFileRepository;
import com.example.quizService.Repository.QuestionRepository;
import com.example.quizService.Util.ExcelValidationUtil;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.example.quizService.Util.ZipExtractorUtil;
import java.io.IOException;

@Service
public class ExcelImportService implements ExcelImportServiceInterface {

    private final QuestionRepository questionRepository;
    private final ExcelImportFileRepository excelImportFileRepository;

    public ExcelImportService(
            QuestionRepository questionRepository,
            ExcelImportFileRepository excelImportFileRepository) {
        this.questionRepository = questionRepository;
        this.excelImportFileRepository = excelImportFileRepository;
    }

    @Override
    @Transactional
    public int importQuestionsFromExcel(MultipartFile file, boolean dryRun) {
        if (file.isEmpty()) {
            throw new ExcelNotFoundException("Ingen Excel-fil hittades att importera");
        }

        MultipartFile excelFile = file; // Börja med att anta att det är en Excel-fil

        // Kolla om det är en ZIP-fil baserat på filnamnet
        String originalFileName = file.getOriginalFilename();
        if (originalFileName != null && originalFileName.toLowerCase().endsWith(".zip")) {
            try {
                // Extrahera Excel-filen från ZIP:en
                // Bilderna sparas automatiskt till static/images/questions/
                excelFile = ZipExtractorUtil.extractAndProcess(file);
                System.out.println("✅ ZIP-fil extraherad. Excel-fil: " + excelFile.getOriginalFilename());
            } catch (IOException e) {
                throw new RuntimeException("Kunde inte extrahera ZIP-filen: " + e.getMessage(), e);
            }
        }

        List<Question> questionsFromExcel = new ArrayList<>();
        List<String> allErrors = new ArrayList<>();

        try (InputStream is = excelFile.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            // Validera header
            ExcelValidationUtil.validateHeader(sheet.getRow(0));

            // Läs rader
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null)
                    continue;

                List<String> rowErrors = ExcelValidationUtil.validateRow(row, r);
                if (!rowErrors.isEmpty()) {
                    allErrors.addAll(rowErrors);
                    continue;
                }

                questionsFromExcel.add(mapRowToQuestion(row));
            }

            if (!allErrors.isEmpty()) {
                throw new RowValidationException(allErrors);
            }

            // Filtrera bort dubletter: både mot DB och inom samma fil
            List<Question> filteredQuestions = new ArrayList<>();
            Set<String> seenQuestions = new HashSet<>();

            for (Question q : questionsFromExcel) {
                // Skapa en unik nyckel baserat på fråga, korrekt svar, ämne och språk
                String key = (q.getQuestion().trim().toLowerCase() + "|"
                        + q.getCorrectAnswer().trim().toLowerCase() + "|"
                        + q.getSubject() + "|"
                        + q.getLang().trim().toLowerCase());

                boolean existsInDB = questionRepository.existsByQuestionAndCorrectAnswerAndSubjectAndLang(
                        q.getQuestion(), q.getCorrectAnswer(), q.getSubject(), q.getLang());

                if (!existsInDB && !seenQuestions.contains(key)) {
                    filteredQuestions.add(q);
                    seenQuestions.add(key);
                }
            }

            if (!dryRun) {
                ExcelImportFile excelFileEntity = new ExcelImportFile();
                excelFileEntity.setFileName(excelFile.getOriginalFilename());
                excelFileEntity.setUploadedAt(LocalDateTime.now());
                excelFileEntity.setDryRun(false);
                excelImportFileRepository.save(excelFileEntity);

                // Koppla frågor till filen
                for (Question q : filteredQuestions) {
                    q.setExcelImportFile(excelFileEntity);
                }

                questionRepository.saveAll(filteredQuestions);
            }

            return filteredQuestions.size();

        } catch (HeaderValidationException | RowValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Oväntat fel vid import av Excel-fil", e);
        }
    }

    @Override
    public List<ExcelFileDTO> getAllExcelFiles() {
        List<ExcelImportFile> files = excelImportFileRepository.findAll();
        return files.stream()
                .map(f -> new ExcelFileDTO(f.getId(), f.getFileName(), f.getUploadedAt(), f.isDryRun()))
                .toList();
    }

    @Override
    @Transactional
    public void deleteExcelFile(Long fileId) {

        ExcelImportFile file = excelImportFileRepository
                .findById(fileId)
                .orElseThrow(() -> new ExcelNotFoundException("Fil hittades inte"));

        // 1. Hämta alla frågor som importerats via denna fil
        List<Question> questions = questionRepository.findByExcelImportFile(file);

        // 2. Koppla loss dem (viktigt!)
        for (Question q : questions) {
            q.setExcelImportFile(null);
        }

        // 3. Spara ändringen (ofta optional men tydligt)
        questionRepository.saveAll(questions);

        // 4. Ta bort Excel-filen (loggen)
        excelImportFileRepository.delete(file);
    }

    // Mappning av rad till Question utan att sätta excelFile (kopplas endast vid
    // faktisk import)
    private Question mapRowToQuestion(Row row) {
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
        q.setImage(row.getCell(28).getStringCellValue());
        q.setSubject((int) row.getCell(29).getNumericCellValue());
        q.setLang(row.getCell(30).getStringCellValue());
        return q;
    }
}
