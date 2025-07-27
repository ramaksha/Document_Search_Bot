package com.example.document_search_bot.service;

import com.example.document_search_bot.util.EmbeddingUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RAGService {

    private final EmbeddingUtil embeddingUtil;
    private final QdrantService qdrantService;

    public RAGService(EmbeddingUtil embeddingUtil, QdrantService qdrantService) {
        this.embeddingUtil = embeddingUtil;
        this.qdrantService = qdrantService;
    }

    public void storeTextWithSource(String source, String text) {
        List<String> chunks = chunkText(text, 500);
        List<List<Double>> embeddings = new ArrayList<>();

        for (String chunk : chunks) {
            embeddings.add(embeddingUtil.getEmbeddings(chunk));
        }

        qdrantService.insertChunks(chunks, embeddings, source);
    }

    public List<String> searchText(String query) {
        List<Double> embedding = embeddingUtil.getEmbeddings(query);
        return qdrantService.searchChunks(embedding, 5);
    }

    public void deleteBySource(String source) {
        qdrantService.deleteBySource(source);
    }

    private List<String> chunkText(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            chunks.add(text.substring(start, end));
            start = end;
        }
        return chunks;
    }
}