package com.myfitnessbuddy.app.config; // Assuming your config package

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class GeminiConfig {

    private String geminiApiKey;
    private final Dotenv dotenv;

    public GeminiConfig() {
        dotenv = Dotenv.configure().ignoreIfMissing().load();
        geminiApiKey = dotenv.get("GEMINI_API_KEY");
        if (geminiApiKey == null) {
            System.err.println("Warning: GEMINI_API_KEY not found in .env file or environment variables.");
        }
    }

    @Bean
    public WebClient geminiWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .defaultUriVariables(java.util.Collections.singletonMap("key", geminiApiKey))
                .build();
    }
}