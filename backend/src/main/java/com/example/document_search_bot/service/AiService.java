package com.example.document_search_bot.service;

import com.example.document_search_bot.dto.GeminiRequest;
import com.example.document_search_bot.dto.GeminiResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient.Builder webClientBuilder;

    private WebClient webClient;
    // A queue to track the last 3 user questions for contextual awareness
// This simulates short-term memory for continuity across questions
    private final Deque<String> contextQueue = new ArrayDeque<>();
    private final int CONTEXT_LIMIT = 3;

    @PostConstruct
    public void init() {
        this.webClient = webClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }


    // Updates the queue by adding the latest question
// Keeps only the most recent CONTEXT_LIMIT questions
    public void updateContext(String newQuestion) {
        if (contextQueue.size() == CONTEXT_LIMIT) {
            contextQueue.removeFirst(); // Remove oldest
        }
        contextQueue.addLast(newQuestion); // Add newest
    }

    // Builds a context block to include in the prompt
// This enhances responses with reference to earlier user queries
    public String buildContextSection() {
        if (contextQueue.isEmpty()) return "";
        StringBuilder sb = new StringBuilder("Recent Questions:\n");
        int i = 1;
        for (String q : contextQueue) {
            sb.append(i++).append(". ").append(q).append("\n");
        }
        return sb.toString().trim();
    }

    public String getGeminiResponse(String fullPrompt)  {

        GeminiRequest.Part part = new GeminiRequest.Part(fullPrompt);
        GeminiRequest.Content content = new GeminiRequest.Content(List.of(part));
        GeminiRequest.GenerationConfig config = new GeminiRequest.GenerationConfig(0.7, 512); // You can tweak tokens

        GeminiRequest request = new GeminiRequest(List.of(content), config);

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/gemini-1.5-flash:generateContent")
                        .queryParam("key", apiKey)
                        .build())
                .bodyValue(request)
                .retrieve()

                .bodyToMono(GeminiResponse.class)
                .map(res -> res.getCandidates().get(0).getContent().getParts().get(0).getText())
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Mono.just("⚠️ Gemini service failed."+e.getMessage());
                })

                .block();
    }
}
