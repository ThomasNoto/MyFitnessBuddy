package com.myfitnessbuddy.app.service;

import com.myfitnessbuddy.app.entity.User;

import io.github.cdimascio.dotenv.Dotenv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class GeminiService {

    private static final Logger logger = Logger.getLogger(GeminiService.class.getName());

    private final WebClient geminiWebClient;

    @Value("${gemini.model}")
    private String geminiModel;

    @Value("${gemini.prompt.preface}")
    private String promptPreface;

    private final Map<String, List<Map<String, Object>>> conversationHistory = new HashMap<>();

    private final UserService userService;

    @Autowired
    public GeminiService(WebClient geminiWebClient, UserService userService) {
        this.geminiWebClient = geminiWebClient;
        this.userService = userService;
    }

    private User getUserData(long id) {
        return userService.getUserById(id);
    }

    public Mono<String> sendMessage(String sessionId, String userMessage, long userId) {
        User currUser = getUserData(userId);
        String dynamicPreface = promptPreface;

        if (currUser != null) {
            dynamicPreface += String.format(" The user's Name is: %s. ", currUser.getName());
            dynamicPreface += String.format(" The user's Age is: %s. ", currUser.getAge());
            dynamicPreface += String.format(" The user's Weight is: %s. ", currUser.getWeight());
            dynamicPreface += String.format(" The user's Height is: %s. ", currUser.getHeight());
            dynamicPreface += String.format(" The user's Fitness Goals is: %s. ", currUser.getFitnessGoals());
            dynamicPreface += String.format(" The user's Gender is: %s. ", currUser.getGender());
            if (currUser.getGender() != null) dynamicPreface += String.format(" The user's Gender is: %s. ", currUser.getGender());
        } else {
             logger.warning("User data not found for ID: " + userId);
             dynamicPreface += " No user-specific data was provided. ";
        }

        List<Map<String, Object>> history = conversationHistory.computeIfAbsent(sessionId, k -> new ArrayList<>());

        // Add current user message to history *before* sending
        history.add(Map.of("role", "user", "parts", List.of(Map.of("text", userMessage))));

        // Construct request body using system_instruction field and contents
        Map<String, Object> systemInstruction = Map.of(
            "parts", List.of(Map.of("text", dynamicPreface))
        );

        List<Map<String, Object>> conversationTurns = new ArrayList<>(history);

        Map<String, Object> requestBody = Map.of(
            "system_instruction", systemInstruction,
            "contents", conversationTurns
        );
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String apiKey = (String) dotenv.get("GEMINI_API_KEY");
        url = url + apiKey;
        System.out.println("URL: " + url);
        logger.info("Sending request to Gemini API with body: " + requestBody);

        return geminiWebClient.post()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> { // === Start of the .map lambda scope ===
                    try { // === Start of the try block ===
                         logger.info("Received response from Gemini API: " + response);

                        // Check for the expected nested structure
                        if (response != null && response.containsKey("candidates")) {
                            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                            if (!candidates.isEmpty() && candidates.get(0).containsKey("content")) {
                                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                                if (content.containsKey("parts")) {
                                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                                    if (!parts.isEmpty() && parts.get(0).containsKey("text")) {
                                        // === Path 1: Successfully extracted AI response ===
                                        String aiResponse = (String) parts.get(0).get("text");
                                        // Add model message to history *after* receiving and processing
                                        history.add(Map.of("role", "model", "parts", List.of(Map.of("text", aiResponse))));
                                        return aiResponse; // === RETURN 1: Returns String ===
                                    }
                                    
                                }
                                
                            }
                            
                        }

                        logger.warning("Unexpected response structure from Gemini API: " + response);
                        return "Error: Could not process the AI response."; // === RETURN 2: Returns String ===

                    } catch (Exception e) { // === Start of the catch block ===
                         // === Path 3: An exception occurred during processing ===
                         logger.log(Level.SEVERE, "Error processing AI response structure:", e);
                         // This path exits the Mono by signaling an error, NOT by returning a String value.
                         throw new RuntimeException("Error processing AI response structure", e); // === THROWS EXCEPTION ===
                    }
                    // === If the try block finishes WITHOUT hitting RETURN 1 or RETURN 2,
                    //     AND no exception was thrown, execution would conceptually reach here.
                    //     There is NO return statement here. This is what the compiler is flagging. ===
                    // However, with RETURN 2 in place after all the 'if' blocks, this point should be unreachable.
                    // The error implies a syntax issue prevents reaching RETURN 2.

                }) // === End of the .map lambda scope ===
                .onErrorResume(ex -> { // Catches errors from upstream (WebClient, onStatus, or the re-thrown RuntimeException from .map)
                    logger.log(Level.SEVERE, "Failed during AI communication or response processing:", ex);

                    if (ex instanceof WebClientResponseException) {
                         WebClientResponseException wcre = (WebClientResponseException) ex;
                         String errorDetails = wcre.getResponseBodyAsString();
                         return Mono.just("Error: AI communication failed with status " + wcre.getStatusCode() + ". Details: " + errorDetails);
                    } else if (ex instanceof RuntimeException && ex.getCause() != null) { // Check getCause() for the original error
                         return Mono.just("Error: Failed to process AI response structure. Details: " + ex.getCause().getMessage());
                    }
                    return Mono.just("Error: An unexpected error occurred during AI communication.");
                });
    }

    public void clearHistory(String sessionId) {
        conversationHistory.remove(sessionId);
        logger.info("Chat history cleared for session: " + sessionId);
    }
}