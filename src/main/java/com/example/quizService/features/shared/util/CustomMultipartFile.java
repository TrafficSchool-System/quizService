package com.example.quizService.features.shared.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * CustomMultipartFile - Wrapper for byte[] to MultipartFile
 * 
 * Purpose:
 * - Convert byte[] (from ZIP file) to MultipartFile
 * - Allow existing Excel import logic to work with extracted files
 * - Used by ZipExtractorUtil
 * 
 * Implementation:
 * - Wraps byte array content
 * - Provides InputStream for Excel reader
 * - Mimics MultipartFile interface
 */
public class CustomMultipartFile implements MultipartFile {

    private final byte[] content; // File content (Excel data)
    private final String name; // Filename

    public CustomMultipartFile(byte[] content, String name) {
        this.content = content;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return name;
    }

    @Override
    public String getContentType() {
        // MIME type for Excel files (.xlsx)
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    @Override
    public boolean isEmpty() {
        return content == null || content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public byte[] getBytes() {
        return content;
    }

    @Override
    public InputStream getInputStream() {
        // Convert byte[] to InputStream for Excel reader
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(File dest) throws IOException {
        // If we need to save file to disk
        try (FileOutputStream fos = new FileOutputStream(dest)) {
            fos.write(content);
        }
    }
}
