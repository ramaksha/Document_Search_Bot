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


import java.util.List;

@Service
@RequiredArgsConstructor
public class AiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient.Builder webClientBuilder;

    private WebClient webClient;


    @PostConstruct
    public void init() {
        this.webClient = webClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
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
