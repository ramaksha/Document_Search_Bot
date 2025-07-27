package com.example.document_search_bot.controller;

import com.example.document_search_bot.service.RAGService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rag")
public class RAGController {

    private final RAGService ragService;

    public RAGController(RAGService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/store")
    public ResponseEntity<String> storeText(
            @RequestParam String source,
            @RequestBody String text
    ) {
        try {
            ragService.storeTextWithSource(source, text);
            return ResponseEntity.ok("Text stored successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<String>> search(@RequestParam String query) {
        try {
            List<String> results = ragService.searchText(query);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteBySource(@RequestParam String source) {
        try {
            ragService.deleteBySource(source);
            return ResponseEntity.ok("Deleted chunks for source: " + source);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error deleting chunks: " + e.getMessage());
        }
    }
}