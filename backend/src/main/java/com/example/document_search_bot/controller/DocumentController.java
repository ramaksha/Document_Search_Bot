package com.example.document_search_bot.controller;

import com.example.document_search_bot.service.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/document")
public class DocumentController {

    private final FileStorageService fileStorageService;

    public DocumentController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }
    @GetMapping
    public ResponseEntity<List<String>> listFiles() {
        List<String> files = fileStorageService.listAllUploadedFiles();
        return ResponseEntity.ok(files);
    }
    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String filename = fileStorageService.storeFile(file);
        return new ResponseEntity<>("file uploaded successfully",HttpStatus.CREATED);
    }
    @DeleteMapping("/{filename}")
    public ResponseEntity<String> deleteFile(@PathVariable String filename) {
        boolean deleted = fileStorageService.deleteFile(filename);
        if (deleted) {
            return ResponseEntity.ok("File deleted successfully: " + filename);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
