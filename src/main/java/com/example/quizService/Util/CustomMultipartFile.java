package com.example.quizService.Util;

import org.springframework.web.multipart.MultipartFile;
import java.io.*;

/**
 * En egen implementation av MultipartFile som låter oss
 * konvertera byte[] (från ZIP-filen) till en MultipartFile
 * som vår befintliga Excel-import kan använda
 */
public class CustomMultipartFile implements MultipartFile {
    
    private final byte[] content;  // Innehållet i filen (Excel-data)
    private final String name;     // Filnamnet
    
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
        // MIME-type för Excel-filer (.xlsx)
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
        // Konverterar byte[] till InputStream som Excel-läsaren förväntar sig
        return new ByteArrayInputStream(content); 
    }
    
    @Override
    public void transferTo(File dest) throws IOException {
        // Om vi behöver spara filen till disk
        try (FileOutputStream fos = new FileOutputStream(dest)) {
            fos.write(content);
        }
    }
}