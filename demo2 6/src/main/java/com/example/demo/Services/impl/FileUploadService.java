package com.example.demo.Services.impl;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.UUID;

@Service
public class FileUploadService {

    private final Path fileStorageLocation;

    public FileUploadService() {
        this.fileStorageLocation = Paths.get("src/main/resources/static/images")
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + "_" + sanitizeFileName(originalFileName);

        // Normalize file name
        fileName = fileName != null ? fileName.trim() : "";

        if (fileName.contains("..")) {
            throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
        }

        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (Exception ex) {
            throw new RuntimeException("File not found " + fileName, ex);
        }
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return null;
        }

        // Normalize and remove accents/diacritics
        fileName = Normalizer.normalize(fileName, Normalizer.Form.NFD);
        fileName = fileName.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        // Replace non-ASCII characters with underscore
        fileName = fileName.replaceAll("[^\\p{ASCII}]", "_");

        // Replace spaces with underscores
        fileName = fileName.replaceAll(" ", "_");

        // Additional sanitization for Turkish characters
        fileName = fileName.replaceAll("ı", "i");
        fileName = fileName.replaceAll("İ", "I");
        fileName = fileName.replaceAll("ç", "c");
        fileName = fileName.replaceAll("Ç", "C");
        fileName = fileName.replaceAll("ğ", "g");
        fileName = fileName.replaceAll("Ğ", "G");
        fileName = fileName.replaceAll("ö", "o");
        fileName = fileName.replaceAll("Ö", "O");
        fileName = fileName.replaceAll("ş", "s");
        fileName = fileName.replaceAll("Ş", "S");
        fileName = fileName.replaceAll("ü", "u");
        fileName = fileName.replaceAll("Ü", "U");

        return fileName;
    }
}
