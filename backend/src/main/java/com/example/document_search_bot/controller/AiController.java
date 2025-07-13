package com.example.document_search_bot.controller;

import com.example.document_search_bot.service.AiService;
import com.example.document_search_bot.service.FileStorageService;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@NoArgsConstructor
@AllArgsConstructor
@RequestMapping("/api/qna")
public class AiController {

    @Autowired
    private AiService aiService;
    @Autowired
    private FileStorageService fileStorageService;
    /**
     *n Spring WebFlux, a Mono<T> is a reactive type that represents a single value (or no value) that may be available now or in the future.
     * Think of it like a promise that either delivers one item or completes empty.
     * @param question
     * @return
     * - It’s the reactive counterpart to Optional<T> or CompletableFuture<T> in traditional Java.
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/ask/{question}")
    public String askFromUploadedFiles(@PathVariable String question) {
        System.out.println("🎯 Controller reached "+question);
        String fileText = fileStorageService.readAllUploadedFileContents();
        String combinedPrompt  =
                "You are an intelligent document search assistant. Answer the QUESTION strictly based on the DOCUMENTS provided.\n\n" +
                        "DOCUMENTS:\n" + fileText + "\n\n" +
                        "QUESTION: " + question + "\n\n" +
                        "Response Format:\n" +
                        "- If the answer is found, mention the source at the end in this format: [Source: Document Name].\n" +
                        "- If no relevant information is found, say: 'I searched the documents but couldn't find relevant information on ... .'\n" +
                        "- Do NOT generate any information that is not in the DOCUMENTS.\n" +
                        "- Keep the response concise and focused, preferably under 200 words." +
                        "- Perform calculations based on data if required.";

        System.out.println("🎯 Combined prompt : "+combinedPrompt);
        return aiService.getGeminiResponse(combinedPrompt);
    }


}
