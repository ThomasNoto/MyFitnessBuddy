package com.myfitnessbuddy.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.myfitnessbuddy.app.entity.User;
import com.myfitnessbuddy.app.service.UserService;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final WebClient geminiWebClient;

    @Value("${gemini.model}")
    private String geminiModel;

    @Value("${gemini.prompt.preface}")
    private String promptPreface;

    private final Map<String, List<Map<String, Object>>> conversationHistory = new HashMap<>();

    @Autowired
    public GeminiService(WebClient geminiWebClient) {
        this.geminiWebClient = geminiWebClient;
    }

    @Autowired
    public UserService userService;

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
        } else {
            dynamicPreface += " No user-specific data was provided. ";
        }

        List<Map<String, Object>> history = conversationHistory.computeIfAbsent(sessionId, k -> new ArrayList<>());
        history.add(Map.of("role", "user", "parts", List.of(Map.of("text", userMessage))));

        List<Map<String, Object>> fullConversation = new ArrayList<>();
        fullConversation.add(Map.of("parts", List.of(Map.of("text", dynamicPreface))));
        fullConversation.addAll(history);

        Map<String, Object> requestBody = Map.of(
                "contents", fullConversation
        );

        return geminiWebClient.post()
                .uri("/{model}:generateContent", geminiModel)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)

                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    if (response != null && response.containsKey("candidates")) {
                        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                        if (!candidates.isEmpty() && candidates.get(0).containsKey("content")) {
                            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                            if (content.containsKey("parts")) {
                                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                                if (!parts.isEmpty() && parts.get(0).containsKey("text")) {
                                    String aiResponse = (String) parts.get(0).get("text");
                                    history.add(Map.of("role", "model", "parts", List.of(Map.of("text", aiResponse))));
                                    return aiResponse;
                                }
                            }
                        }
                    }
                    return "Error: Could not process the AI response.";
                })
                .onErrorReturn("Error: Failed to communicate with the AI.");
    }

    public void clearHistory(String sessionId) {
        conversationHistory.remove(sessionId);
    }
}