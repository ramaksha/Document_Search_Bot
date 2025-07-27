package com.example.document_search_bot.controller;

import com.example.document_search_bot.service.AiService;
import com.example.document_search_bot.service.FileStorageService;
import com.example.document_search_bot.service.RAGService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.print.event.PrintEvent;

@RestController
@NoArgsConstructor
@AllArgsConstructor
@RequestMapping("/api/qna")
public class AiController {

    @Autowired
    private AiService aiService;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private RAGService ragService;
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
        System.out.println("🎯 Controller reached: " + question);
        //updating for context
        aiService.updateContext(question);
        // Semantic search using RAG
        List<String> relevantChunks = ragService.searchText(question);
         for(String i:relevantChunks)
            System.out.println(i);
        if (relevantChunks.isEmpty()) {
            return "I searched the documents but couldn't find relevant information on \"" + question + "\".";
        }

        //  Build prompt from retrieved chunks
        StringBuilder documentBuilder = new StringBuilder();
        for (String chunk : relevantChunks) {
            documentBuilder.append(chunk.trim()).append("\n\n");
        }

        String combinedPrompt =
                "You are an intelligent document search assistant. Answer the QUESTION strictly based on the DOCUMENTS provided.\n\n" +

                        "DOCUMENTS:\n" + documentBuilder.toString().trim() + "\n\n" +
                        "QUESTION: " + question + "\n\n" +
                        "Response Format:\n" +
                        "-if user asks about file meta data say:Metadata queries aren't supported yet. Please try asking about the document content instead.\n"+
                        "-if user ask about entire page or page summery say: Sorry, I can't return full pages or entire files summery directly. Try asking a specific question about the content.\n"+
                        "- If the answer is found, mention the source at the end in this format: [Source: Document Name].\n" +
                        "- If no relevant information is found, say: 'I searched the documents but couldn't find relevant information on ... .'\n" +
                        "- Do NOT generate any information that is not in the DOCUMENTS.\n" +
                        "- Keep the response concise and focused, preferably under 200 words.\n" +
                        "- Perform calculations based on data if required.";

        System.out.println(" Combined prompt:\n" + combinedPrompt);

        return aiService.getGeminiResponse(combinedPrompt);
    }


}
