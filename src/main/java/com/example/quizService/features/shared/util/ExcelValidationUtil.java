package com.example.quizService.features.shared.util;

import com.example.quizService.features.shared.exception.HeaderValidationException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.List;

/**
 * ExcelValidationUtil - Validate Excel file structure and content
 * 
 * Purpose:
 * - Validate Excel header row (column names and order)
 * - Validate Excel data rows (required fields, data types, formats)
 * - Used by ImportQuestionsUseCase
 * 
 * Validation Rules:
 * - Header: 31 columns with exact names and order
 * - Questions: Text, not numbers
 * - Answers: 1 correct + 3 wrong, all text
 * - Driver's license fields: 0 or 1 only
 * - Subject: 1-5 (integer)
 * - Language: Letters only (no digits)
 * - Image (UPDATED): Can be filename OR URL (no file existence validation for
 * URLs)
 */
public class ExcelValidationUtil {

    /**
     * Check if a row is completely empty (all cells are null or blank)
     * 
     * @param row Excel row to check
     * @return true if row is empty, false otherwise
     */
    public static boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }

        for (int i = 0; i < 31; i++) {
            if (row.getCell(i) != null && row.getCell(i).getCellType() != CellType.BLANK) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validate Excel header row
     * 
     * @param headerRow Header row from Excel sheet
     * @throws HeaderValidationException if header is invalid
     */
    public static void validateHeader(Row headerRow) {
        String[] expectedHeaders = {
                "ID", "QUESTION", "SFI", "CORRECT ANSWER", "WRONG ANSWER 1", "WRONG ANSWER 2", "WRONG ANSWER 3",
                "EXPLANATION FOR THE STUDENT", "A", "AM", "B", "BE", "C", "CE", "D", "DE", "YKB-C", "YKB-D", "ADR",
                "VTL", "TA1I-1", "TA1I-2", "TA1I-3", "TA1I-4", "TA1I-5", "APV", "YRS", "1-TRA", "IMAGE", "SUBJECT",
                "LANG"
        };

        for (int i = 0; i < expectedHeaders.length; i++) {
            if (headerRow.getCell(i) == null
                    || !headerRow.getCell(i).getStringCellValue().trim().equalsIgnoreCase(expectedHeaders[i])) {
                throw new HeaderValidationException(
                        "Invalid header row! Column " + (i + 1) + " should be: " + expectedHeaders[i]);
            }
        }
    }

    /**
     * Validate Excel data row
     * 
     * @param row      Data row from Excel sheet
     * @param rowIndex Row index (1-based)
     * @return List of validation errors (empty if valid)
     */
    public static List<String> validateRow(Row row, int rowIndex) {
        List<String> errors = new ArrayList<>();

        int expectedColumns = 31;

        // Check all cells exist
        for (int i = 0; i < expectedColumns; i++) {
            if (row.getCell(i) == null) {
                errors.add("Column " + (i + 1) + " missing on row: " + (rowIndex + 1));
            }
        }

        // QUESTION (column 1)
        boolean questionMissing = row.getCell(1) == null ||
                row.getCell(1).getCellType() == CellType.BLANK ||
                row.getCell(1).getCellType() != CellType.STRING ||
                row.getCell(1).getStringCellValue().trim().isEmpty();
        if (questionMissing) {
            errors.add("Question text (QUESTION) missing on row: " + (rowIndex + 1));
        } else if (row.getCell(1).getStringCellValue().trim().matches("^\\d+$")) {
            errors.add("Question text (QUESTION) must not be only digits on row: " + (rowIndex + 1));
        }

        // SFI (column 2)
        boolean sfiMissing = row.getCell(2) == null ||
                row.getCell(2).getCellType() == CellType.BLANK ||
                row.getCell(2).getCellType() != CellType.STRING ||
                row.getCell(2).getStringCellValue().trim().isEmpty();
        if (sfiMissing) {
            errors.add("SFI missing on row: " + (rowIndex + 1));
        } else if (row.getCell(2).getStringCellValue().trim().matches("^\\d+$")) {
            errors.add("SFI must not be only digits on row: " + (rowIndex + 1));
        }

        // CORRECT ANSWER (column 3)
        boolean correctAnswerMissing = row.getCell(3) == null ||
                row.getCell(3).getCellType() == CellType.BLANK ||
                row.getCell(3).getCellType() != CellType.STRING ||
                row.getCell(3).getStringCellValue().trim().isEmpty();
        if (correctAnswerMissing) {
            errors.add("Correct answer (CORRECT ANSWER) missing on row: " + (rowIndex + 1));
        } else if (row.getCell(3).getStringCellValue().trim().matches("^\\d+$")) {
            errors.add("CORRECT ANSWER must not be only digits on row: " + (rowIndex + 1));
        }

        // WRONG ANSWERS 1-3 (columns 4-6)
        for (int i = 4; i <= 6; i++) {
            boolean wrongAnswerMissing = row.getCell(i) == null ||
                    row.getCell(i).getCellType() == CellType.BLANK ||
                    row.getCell(i).getCellType() != CellType.STRING ||
                    row.getCell(i).getStringCellValue().trim().isEmpty();

            if (wrongAnswerMissing) {
                errors.add("Wrong answer (WRONG ANSWER " + (i - 3) + ") missing on row: " + (rowIndex + 1));
            } else if (row.getCell(i).getStringCellValue().trim().matches("^\\d+$")) {
                errors.add("WRONG ANSWER must not be only digits on row: " + (rowIndex + 1));
            }
        }

        // EXPLANATION FOR STUDENT (column 7)
        boolean explanationMissing = row.getCell(7) == null ||
                row.getCell(7).getCellType() == CellType.BLANK ||
                row.getCell(7).getCellType() != CellType.STRING ||
                row.getCell(7).getStringCellValue().trim().isEmpty();
        if (explanationMissing) {
            errors.add("EXPLANATION FOR THE STUDENT missing on row: " + (rowIndex + 1));
        } else if (row.getCell(7).getStringCellValue().trim().matches("^\\d+$")) {
            errors.add("EXPLANATION FOR THE STUDENT must not be only digits on row: " + (rowIndex + 1));
        } else if (row.getCell(7).getStringCellValue().length() > 2000) {
            errors.add("EXPLANATION FOR THE STUDENT is too long on row: " + (rowIndex + 1));
        }

        // DRIVER'S LICENSE CATEGORIES (columns 8-27)
        // Must be 0 or 1 only
        for (int i = 8; i <= 27; i++) {
            if (row.getCell(i) == null || row.getCell(i).getCellType() == CellType.BLANK) {
                errors.add("Permission column (" + (i + 1) + ") missing on row: " + (rowIndex + 1));
            } else if (row.getCell(i).getCellType() != CellType.NUMERIC) {
                errors.add("Permission column (" + (i + 1) + ") has wrong data type on row: " + (rowIndex + 1));
            } else {
                double val = row.getCell(i).getNumericCellValue();
                if (val != 0 && val != 1) {
                    errors.add("Permission column (" + (i + 1) + ") must be 0 or 1 on row: " + (rowIndex + 1));
                }
            }
        }

        // IMAGE (column 28)
        // UPDATED: Can be filename OR URL - no validation needed!
        // Frontend handles both formats automatically

        // SUBJECT (column 29)
        if (row.getCell(29) == null ||
                row.getCell(29).getCellType() == CellType.BLANK ||
                row.getCell(29).getCellType() != CellType.NUMERIC) {
            errors.add("SUBJECT missing or has wrong data type on row: " + (rowIndex + 1));
        } else {
            double subjectValue = row.getCell(29).getNumericCellValue();
            if (subjectValue < 1 || subjectValue > 5 || subjectValue != Math.floor(subjectValue)) {
                errors.add("SUBJECT must be an integer between 1 and 5 on row: " + (rowIndex + 1));
            }
        }

        // LANG (column 30)
        if (row.getCell(30) == null || row.getCell(30).getCellType() == CellType.BLANK) {
            errors.add("LANG missing on row: " + (rowIndex + 1));
        } else if (row.getCell(30).getCellType() != CellType.STRING) {
            errors.add("LANG must be text (not numbers) on row: " + (rowIndex + 1));
        } else if (row.getCell(30).getStringCellValue().trim().isEmpty()) {
            errors.add("LANG missing on row: " + (rowIndex + 1));
        } else if (!row.getCell(30).getStringCellValue().trim().matches("^[A-Za-zÅÄÖåäö]+$")) {
            errors.add("LANG may only contain letters (no digits or special characters) on row: " + (rowIndex + 1));
        }

        return errors;
    }
}
