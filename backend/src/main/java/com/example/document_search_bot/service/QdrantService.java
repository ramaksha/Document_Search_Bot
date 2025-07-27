package com.example.document_search_bot.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.*;

@Service
public class QdrantService {

    private WebClient webClient;

    @Value("${qdrant.collection.name}")
    private String collectionName;

    @Value("${qdrant.instance.key}")
    private String key;

    @Value("${qdrant.base.url}")
    private String baseUrl;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("api-key", key)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofSeconds(7)) // ⏱️ Timeout

                ))

                .build();
    }

    public void insertChunks(List<String> chunks, List<List<Double>> embeddings, String source) {
        List<Map<String, Object>> points = new ArrayList<>();

        for (int i = 0; i < chunks.size(); i++) {
            Map<String, Object> payload = Map.of(
                    "chunk", chunks.get(i),
                    "source", source
            );

            Map<String, Object> point = Map.of(
                    "id", UUID.randomUUID().toString(),
                    "vector", embeddings.get(i),
                    "payload", payload
            );

            points.add(point);
        }

        Map<String, Object> requestBody = Map.of("points", points);

        try {
            webClient.put()
                    .uri("/collections/" + collectionName + "/points")
                    .bodyValue(requestBody)
                    .retrieve()
                    .toBodilessEntity()
                    .onErrorResume(e -> {
                        e.printStackTrace();
                        return Mono.error(new RuntimeException("Error inserting into Qdrant", e));
                    })
                    .block();
        } catch (WebClientResponseException ex) {
            throw new RuntimeException("Insert failed: " + ex.getResponseBodyAsString(), ex);
        }
    }

    public void deleteBySource(String source) {
        Map<String, Object> filter = Map.of(
                "must", List.of(Map.of(
                        "key", "source",
                        "match", Map.of("value", source)
                ))
        );

        Map<String, Object> requestBody = Map.of("filter", filter);

        try {
            webClient.post()
                    .uri("/collections/" + collectionName + "/points/delete")
                    .bodyValue(requestBody)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException ex) {
            throw new RuntimeException("Delete failed: " + ex.getResponseBodyAsString(), ex);
        }
    }

    public List<String> searchChunks(List<Double> embedding, int limit) {
        Map<String, Object> requestBody = Map.of(
                "vector", embedding,
                "limit", limit,
                "with_payload", true
        );

        try {
            Map<String, Object> response = webClient.post()
                    .uri("/collections/" + collectionName + "/points/search")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || !response.containsKey("result")) {
                throw new RuntimeException("No search results found.");
            }

            List<?> results = (List<?>) response.get("result");
            List<String> chunks = new ArrayList<>();

            for (Object res : results) {
                Map<?, ?> result = (Map<?, ?>) res;
                Double score = Double.parseDouble(result.get("score").toString());

                if (score < 0.65) continue; // 🧠 Threshold filter

                Map<?, ?> payload = (Map<?, ?>) result.get("payload");
                if (payload != null && payload.containsKey("chunk")) {
                    chunks.add(payload.get("chunk").toString());
                }
            }
            return chunks;
        } catch (WebClientResponseException ex) {
            throw new RuntimeException("Search failed: " + ex.getResponseBodyAsString(), ex);
        }
    }
}