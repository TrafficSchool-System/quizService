package com.example.quizService.Util;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import com.example.quizService.Exception.HeaderValidationException;

public class ExcelValidationUtil {

    // === VALIDERA HEADERN ===
    public static void validateHeader(Row headerRow) {
        String[] expectedHeaders = {
                // Rubrikerna som vi förväntar oss i Excelfilen
                "ID", "QUESTION", "SFI", "CORRECT ANSWER", "WRONG ANSWER 1", "WRONG ANSWER 2", "WRONG ANSWER 3",
                "EXPLANATION FOR THE STUDENT", "A", "AM", "B", "BE", "C", "CE", "D", "DE", "YKB-C", "YKB-D", "ADR",
                "VTL", "TA1I-1", "TA1I-2", "TA1I-3", "TA1I-4", "TA1I-5", "APV", "YRS", "1-TRA", "IMAGE", "SUBJECT",
                "LANG"
        };

        // Loopa igenom varje förväntad kolumnrubrik (en kolumn i taget)
        for (int i = 0; i < expectedHeaders.length; i++) {

            // Hämta cellen i header-raden på kolumnposition 'i'
            // Kontrollera om:
            // 1. Cellen är null (tom)
            // 2. eller om texten i cellen inte matchar förväntade rubriken.
            if (headerRow.getCell(i) == null
                    || !headerRow.getCell(i).getStringCellValue().trim().equalsIgnoreCase(expectedHeaders[i])) {
                throw new HeaderValidationException(
                        "Felaktig header-rad! Kolumn " + (i + 1) + " ska vara: " + expectedHeaders[i]);

            }
        }
    }

    // === VALIDERA RADEN ===
    public static List<String> validateRow(Row row, int rowIndex) {
        List<String> errors = new ArrayList<>();

        // Antal kolumner i excelfilen
        int expectedColumns = 31;

        // Kontrollera att alla celler finns
        for (int i = 0; i < expectedColumns; i++) {
            if (row.getCell(i) == null) {
                errors.add("Kolumn " + (i + 1) + " saknas på rad: " + (rowIndex + 1));
            }

        }

        // QUESTION
        // Kolla om frågetexten ("QUESTION") finns på index 1 i excelfilen
        boolean questionMissing = row.getCell(1) == null ||
                row.getCell(1).getCellType() == CellType.BLANK ||
                row.getCell(1).getCellType() != CellType.STRING ||
                row.getCell(1).getStringCellValue().trim().isEmpty();
        if (questionMissing) {

            // Felmeddelande
            errors.add("Frågetext (QUESTION) saknas på rad: " + (rowIndex + 1));

        } else if (row.getCell(1).getStringCellValue().trim().matches("^\\d+$")) {

            // Felmeddelande
            errors.add("Frågetext (QUESTION) får inte bara vara siffror på rad: " + (rowIndex + 1));
        }

        // SFI
        // Kolla om ("SFI") finns på index 2 i excelfilen
        boolean sfiMissing = row.getCell(2) == null ||
                row.getCell(2).getCellType() == CellType.BLANK ||
                row.getCell(2).getCellType() != CellType.STRING ||
                row.getCell(2).getStringCellValue().trim().isEmpty();
        if (sfiMissing) {
            // Felmeddelande
            errors.add("SFI saknas på rad: " + (rowIndex + 1));

        } else if (row.getCell(2).getStringCellValue().trim().matches("^\\d+$")) {
            // Felmeddelande
            errors.add("SFI får inte bara var siffror på rad: " + (rowIndex + 1));
        }

        // CORRECT ANSWER
        // Kolla om ("CORRECT ANSWER") finns på index 3 i excelfilen
        boolean correctAnswerMissing = row.getCell(3) == null ||
                row.getCell(3).getCellType() == CellType.BLANK ||
                row.getCell(3).getCellType() != CellType.STRING ||
                row.getCell(3).getStringCellValue().trim().isEmpty();
        if (correctAnswerMissing) {
            // Felmeddelande
            errors.add("Korrekt svar (CORRECT ANSWER) saknas på rad: " + (rowIndex + 1));

        } else if (row.getCell(3).getStringCellValue().trim().matches("^\\d+$")) {
            // Felmeddelande
            errors.add("CORRECT ANSWER får inte bara vara siffror på rad: " + (rowIndex + 1));
        }

        // WRONG ANSWER 1-3
        // Kolla om ("WRONG ANSWER 1-3") finns på index 4-6 i excelfilen
        for (int i = 4; i <= 6; i++) {
            boolean wrongAnswerMissing = row.getCell(i) == null ||
                    row.getCell(i).getCellType() == CellType.BLANK ||
                    row.getCell(i).getCellType() != CellType.STRING ||
                    row.getCell(i).getStringCellValue().trim().isEmpty();

            if (wrongAnswerMissing) {
                // Felmeddelande
                errors.add("Felaktig svar (WRONG ANSWER " + (i + 3) + ") saknas på rad: " + (rowIndex + 1));

            } else if (row.getCell(i).getStringCellValue().trim().matches("^\\d+$")) {
                // Felmeddelande
                errors.add("WRONG ANSWER får inte bara vara siffror på rad: " + (rowIndex + 1));
            }
        }

        // EXPLINATION FOR THE STUDENT
        // Kontrollerar om EXPLINATION FOR STUDENT är längre än 2000 tecken
        boolean explinationMissing = row.getCell(7) == null ||
                row.getCell(7).getCellType() == CellType.BLANK ||
                row.getCell(7).getCellType() != CellType.STRING ||
                row.getCell(7).getStringCellValue().trim().isEmpty();
        if (explinationMissing) {

            // Felmeddelande
            errors.add("EXPLINATION FOR THE STUDENT saknas på rad: " + (rowIndex + 1));
        } else if (row.getCell(7).getStringCellValue().trim().matches("^\\d+$")) {

            // Felmeddelande
            errors.add("EXPLINATION FOR THE STUDENT får inte bara vara siffror på rad: " + (rowIndex + 1));

        } else if (row.getCell(7).getStringCellValue().length() > 2000) {

            // Felmeddelande
            errors.add("EXPLINATION FOR THE STUDENT är för lång på rad: " + (rowIndex + 1));
        }

        // BEHÖRIGHETER
        // Kollar om alla behörighetskolumner bara innehåller siffran 0 eller 1 samt om
        // det är tomt
        for (int i = 8; i <= 27; i++) {
            if (row.getCell(i) == null || row.getCell(i).getCellType() == CellType.BLANK) {

                // Felmeddelande
                errors.add("Behörighetskolumn (" + (i + 1) + ") saknas på rad: " + (rowIndex + 1));

            } else if (row.getCell(i).getCellType() != CellType.NUMERIC) {

                // Felmeddelande
                errors.add("Behörighetskolumn (" + (i + 1) + ") har fel datatyp på rad: " + (rowIndex + 1));

            } else {

                double val = row.getCell(i).getNumericCellValue();
                if (val != 0 && val != 1) {

                    // Felmeddelande
                    errors.add("Behörighetskolumn (" + (i + 1) + ") måste vara 0 eller 1 på rad: " + (rowIndex + 1));
                }
            }
        }

        // IMAGE
        /*
         * if (row.getCell(28).getStringCellValue().trim().isEmpty()) {
         * throw new RowValidationException("IMAGE saknas på rad " + (rowIndex + 1));
         * }
         */

        validateImageExists(row.getCell(28).getStringCellValue(), rowIndex, errors);

        // SUBJECT
        if (row.getCell(29) == null ||
                row.getCell(29).getCellType() == CellType.BLANK ||
                row.getCell(29).getCellType() != CellType.NUMERIC) {

            // Felmeddelande
            errors.add("SUBJECT saknas eller har fel datatyp på rad: " + (rowIndex + 1));

        } else {
            double subjectValue = row.getCell(29).getNumericCellValue();

            if (subjectValue < 1 || subjectValue > 5 || subjectValue != Math.floor(subjectValue)) {
                // Felmeddelande
                errors.add("SUBJECT måste vara ett heltal mellan 1 och 5 på rad: " + (rowIndex + 1));
            }
        }

        // LANG
        if (row.getCell(30) == null || row.getCell(30).getCellType() == CellType.BLANK) {
            errors.add("LANG saknas på rad: " + (rowIndex + 1));
        } else if (row.getCell(30).getCellType() != CellType.STRING) {
            errors.add("LANG måste vara text (inte siffror) på rad: " + (rowIndex + 1));
        } else if (row.getCell(30).getStringCellValue().trim().isEmpty()) {
            errors.add("LANG saknas på rad: " + (rowIndex + 1));
        } else if (!row.getCell(30).getStringCellValue().trim().matches("^[A-Za-zÅÄÖåäö]+$")) {
            errors.add(
                    "LANG får bara innehålla bokstäver (inga siffror eller specialtecken) på rad: " + (rowIndex + 1));
        }

        return errors;
    }

    // I ExcelValidationUtil.java, lägg till denna metod:

    public static void validateImageExists(String imageName, int rowIndex, List<String> errors) {
        if (imageName == null || imageName.trim().isEmpty()) {
            return; // Tom bild är OK
        }

        // Kontrollera om filen finns
        try {
            java.nio.file.Path imagePath = java.nio.file.Paths.get(
                    "src/main/resources/static/images/questions/" + imageName.trim());

            if (!java.nio.file.Files.exists(imagePath)) {
                errors.add("Bilden '" + imageName + "' finns inte på rad: " + (rowIndex + 1));
            }
        } catch (Exception e) {
            errors.add("Kunde inte validera bild '" + imageName + "' på rad: " + (rowIndex + 1));
        }
    }

}
