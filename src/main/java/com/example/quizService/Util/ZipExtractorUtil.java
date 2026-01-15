package com.example.quizService.Util;

import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Utility-klass för att extrahera ZIP-filer som innehåller:
 * 1. En Excel-fil (.xlsx)
 * 2. En images/ mapp med bilder
 */
public class ZipExtractorUtil {

    /**
     * Extraherar ZIP-filen och returnerar Excel-filen som en MultipartFile.
     * Bilderna sparas automatiskt till static/images/questions/
     * 
     * @param zipFile - Den uppladdade ZIP-filen från admin
     * @return MultipartFile - Excel-filen som kan användas i befintlig import-logik
     */
    public static MultipartFile extractAndProcess(MultipartFile zipFile) throws IOException {
        
        // Skapa en temporär mapp för att lagra Excel-filen under extraktionen
        Path tempDir = Files.createTempDirectory("quiz-import-");
        MultipartFile excelFile = null;
        
        // Målmappen där vi sparar bilderna permanent
        Path imageDestination = Paths.get("src/main/resources/static/images/questions");
        Files.createDirectories(imageDestination);  // Skapar mappen om den inte finns

        // Öppna ZIP-filen för läsning
        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
            ZipEntry entry;
            
            // Loopa igenom varje fil i ZIP:en
            while ((entry = zis.getNextEntry()) != null) {
                String fileName = entry.getName();
                
                // Skippa macOS metadata-filer (börjar med __MACOSX eller .)
                if (fileName.startsWith("__MACOSX") || fileName.startsWith(".")) {
                    continue;
                }
                
                // Skippa mappar (vi vill bara ha filer)
                if (entry.isDirectory()) {
                    continue;
                }
                
                // KAN DETTA VARA EXCEL-FILEN?
                if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                    // Spara Excel-filen temporärt
                    Path excelPath = tempDir.resolve("temp.xlsx");
                    Files.copy(zis, excelPath);
                    
                    // Konvertera till MultipartFile så vi kan använda den i befintlig kod
                    excelFile = new CustomMultipartFile(
                        Files.readAllBytes(excelPath), 
                        fileName
                    );
                }
                // KAN DETTA VARA EN BILD?
                else if (isImageFile(fileName)) {
                    // Extrahera bara filnamnet (ta bort "images/" prefix om det finns)
                    String imageFileName = Paths.get(fileName).getFileName().toString();
                    
                    // Spara bilden till vårt permanenta bildbibliotek
                    Path imagePath = imageDestination.resolve(imageFileName);
                    Files.copy(zis, imagePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    
                    System.out.println("✅ Sparade bild: " + imageFileName);
                }
                
                zis.closeEntry();  // Stäng denna entry och gå till nästa
            }
        }
        
        // Säkerhetskontroll: Fanns det en Excel-fil i ZIP:en?
        if (excelFile == null) {
            throw new IOException("Ingen Excel-fil hittades i ZIP-filen");
        }
        
        return excelFile;
    }
    
    /**
     * Hjälpmetod: Kollar om en fil är en bild baserat på filändelsen
     */
    private static boolean isImageFile(String filename) {
        String lower = filename.toLowerCase();
        return lower.endsWith(".jpg") || 
               lower.endsWith(".jpeg") || 
               lower.endsWith(".png") || 
               lower.endsWith(".gif") ||
               lower.endsWith(".svg");
    }
}