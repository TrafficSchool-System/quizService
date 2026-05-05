package com.example.quizService.features.shared.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * ZipExtractorUtil - Extract Excel and images from ZIP file
 * 
 * Purpose:
 * - Extract Excel file from ZIP
 * - Save images to resources/static/images/questions/
 * - Support bulk import with images
 * 
 * Flow:
 * 1. Create temp directory for Excel file
 * 2. Loop through ZIP entries
 * 3. Extract Excel file (*.xlsx)
 * 4. Save images to permanent location
 * 5. Return Excel file as MultipartFile
 * 
 * Supported Image Formats:
 * - jpg, jpeg, png, gif, svg
 * 
 * Note:
 * - Skips macOS metadata files (__MACOSX, .)
 * - Skips directories
 * - Images from ZIP are saved locally
 * - Excel can also reference external URLs (new feature)
 */
public class ZipExtractorUtil {

    private static final Logger log = LoggerFactory.getLogger(ZipExtractorUtil.class);

    /**
     * Extract ZIP file and return Excel file
     * Images are saved automatically to static/images/questions/
     * 
     * @param zipFile Uploaded ZIP file from admin
     * @return MultipartFile - Excel file for import
     * @throws IOException if extraction fails or no Excel found
     */
    public static MultipartFile extractAndProcess(MultipartFile zipFile) throws IOException {

        // Create temp directory for Excel file
        Path tempDir = Files.createTempDirectory("quiz-import-");
        MultipartFile excelFile = null;

        // Target directory for images (permanent storage)
        Path imageDestination = Paths.get("src/main/resources/static/images/questions");
        Files.createDirectories(imageDestination); // Create if not exists

        // Open ZIP file for reading
        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
            ZipEntry entry;

            // Loop through each file in ZIP
            while ((entry = zis.getNextEntry()) != null) {
                String fileName = entry.getName();

                // Skip macOS metadata files (start with __MACOSX or .)
                if (fileName.startsWith("__MACOSX") || fileName.startsWith(".")) {
                    continue;
                }

                // Skip directories (we only want files)
                if (entry.isDirectory()) {
                    continue;
                }

                // IS THIS THE EXCEL FILE?
                if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                    // Save Excel file temporarily
                    Path excelPath = tempDir.resolve("temp.xlsx");
                    Files.copy(zis, excelPath);

                    // Convert to MultipartFile for existing import logic
                    excelFile = new CustomMultipartFile(
                            Files.readAllBytes(excelPath),
                            fileName);
                }
                // IS THIS AN IMAGE?
                else if (isImageFile(fileName)) {
                    // Extract just filename (remove "images/" prefix if exists)
                    String imageFileName = Paths.get(fileName).getFileName().toString();

                    // Save image to permanent library
                    Path imagePath = imageDestination.resolve(imageFileName);
                    Files.copy(zis, imagePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                    log.info("Saved image: {}", imageFileName);
                }

                zis.closeEntry(); // Close this entry and move to next
            }
        }

        // Safety check: Was there an Excel file in the ZIP?
        if (excelFile == null) {
            throw new IOException("No Excel file found in ZIP file");
        }

        return excelFile;
    }

    /**
     * Helper method: Check if file is an image based on extension
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
