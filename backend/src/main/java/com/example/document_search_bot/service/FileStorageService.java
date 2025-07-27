package com.example.document_search_bot.service;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileStorageService {

    private final RAGService ragService;
    private final Tika tika = new Tika();

    @Value("${storage.file.path}")
    private String UPLOAD_DIR;

    public FileStorageService(RAGService ragService) {
        this.ragService = ragService;
    }


    public String storeFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || !isSupportedFile(originalFilename)) {
            throw new IllegalArgumentException("Unsupported file type. Only PDF, DOCX, XLSX, and PPTX are allowed.");
        }

        long maxSize = 2 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 2MB.");
        }

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (Files.notExists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path destination = uploadPath.resolve(originalFilename);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            // 🧠 Extract text and store in Qdrant
            String extractedText = tika.parseToString(file.getInputStream());
            ragService.storeTextWithSource(originalFilename, extractedText);

            return originalFilename;

        } catch (IOException | TikaException e) {
            throw new RuntimeException("Failed to store and index file: " + originalFilename, e);
        }
    }

    /**
     * Deletes a file and its associated chunks from Qdrant.
     */
    public boolean deleteFile(String filename) {
        Path filePath = Paths.get(UPLOAD_DIR).resolve(filename);

        try {
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                ragService.deleteBySource(filename);
            }
            return deleted;
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file: " + filename, e);
        }
    }

    /**
     * Lists all uploaded files in the directory.
     */
    public List<String> listAllUploadedFiles() {
        try (Stream<Path> files = Files.list(Paths.get(UPLOAD_DIR))) {
            return files
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Could not list uploaded files", e);
        }
    }

    /**
     * Reads and extracts text from all uploaded files.
     * Prepends each file’s name to its content.
     */
    public String readAllUploadedFileContents() {
        StringBuilder promptBuilder = new StringBuilder();

        try (Stream<Path> files = Files.list(Paths.get(UPLOAD_DIR))) {
            files.filter(Files::isRegularFile)
                    .filter(this::isSupportedFile)
                    .forEach(path -> {
                        try {
                            String content = tika.parseToString(path.toFile());
                            promptBuilder.append("From file: ").append(path.getFileName()).append("\n");
                            promptBuilder.append(content.trim()).append("\n\n");
                        } catch (IOException | TikaException e) {
                            promptBuilder.append("Failed to read file: ")
                                    .append(path.getFileName())
                                    .append("\n\n");
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("Could not read uploaded files", e);
        }

        return promptBuilder.toString().trim();
    }

    /**
     * Checks if a file extension is supported (by filename).
     */
    private boolean isSupportedFile(String filename) {
        String lower = filename.toLowerCase();
        return lower.endsWith(".pdf") || lower.endsWith(".docx")
                || lower.endsWith(".xlsx") || lower.endsWith(".pptx") || lower.endsWith(".txt");
    }

    /**
     * Overload for Stream filtering – checks if a Path’s filename is supported.
     */
    private boolean isSupportedFile(Path path) {
        return isSupportedFile(path.getFileName().toString());
    }
}