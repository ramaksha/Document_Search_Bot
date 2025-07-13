package com.example.document_search_bot;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;

@OpenAPIDefinition(
		info = @Info(
				title = "Document Search Bot API",
				version = "1.0",
				description = "Swagger documentation for GenAI document search service"
		)
)

@SpringBootApplication(exclude = {WebFluxAutoConfiguration.class})
public class DocumentSearchBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentSearchBotApplication.class, args);
	}

}
