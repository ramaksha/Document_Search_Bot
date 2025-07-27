package com.example.document_search_bot.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class EmbeddingUtil {

    private final WebClient webClient;

    public EmbeddingUtil() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:11434") // Ollama local embedding endpoint
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public List<Double> getEmbeddings(String text) {
        try {
            Map<String, Object> requestBody = Map.of(
                    "model", "nomic-embed-text",
                    "prompt", text
            );

            Map<String, Object> response = webClient.post()
                    .uri("/api/embeddings")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(); // synchronous

            if (response == null || !response.containsKey("embedding")) {
                throw new RuntimeException("No embedding found in response");
            }

            Object embeddingRaw = response.get("embedding");

            if (!(embeddingRaw instanceof List<?> list)) {
                throw new RuntimeException("Unexpected format for embedding");
            }

            return list.stream()
                    .map(obj -> ((Number) obj).doubleValue())
                    .collect(Collectors.toList());

        } catch (WebClientResponseException ex) {
            throw new RuntimeException("WebClientResponseException: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString(), ex);
        } catch (Exception ex) {
            throw new RuntimeException("Unexpected error while fetching embeddings: " + ex.getMessage(), ex);
        }
    }
}

