package com.myfitnessbuddy.app.controller;

import com.myfitnessbuddy.app.service.*;
// import jakarta.servlet.http.HttpSession; // Removed

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestParam; // Removed
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/ai/chat") // Added a base request mapping
public class ChatController {

    private final GeminiService geminiService;

    @Autowired
    public ChatController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/send") // Updated mapping
    public Mono<ResponseEntity<Map<String, String>>> sendMessage(@RequestBody Map<String, String> payload /* Removed @RequestParam sessionId, HttpSession session */) {

        // --- Get userId and message from the request body ---
        Long userId = null;
        String userIdStr = payload.get("userId");
        if (userIdStr != null) {
            try {
                userId = Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                // Handle the case where userId is not a valid number
                return Mono.just(ResponseEntity.badRequest().body(Map.of("error", "Invalid userId format.")));
            }
        } else {
             // Handle the case where userId is missing from the body
            return Mono.just(ResponseEntity.badRequest().body(Map.of("error", "userId is required in the request body.")));
        }

        String userMessage = payload.get("message");
        // --- End of getting data from body ---


        if (userMessage != null && !userMessage.trim().isEmpty()) {
            // For a simple student project, you can use the userId itself
            // as the session identifier for the GeminiService.
            // This means each user has their own chat history tied to their ID.
            String sessionIdForService = userId.toString();

            return geminiService.sendMessage(sessionIdForService, userMessage, userId)
                    .map(aiResponse -> ResponseEntity.ok(Map.of("response", aiResponse)));
        } else {
            return Mono.just(ResponseEntity.badRequest().body(Map.of("error", "Message cannot be empty.")));
        }
    }

    @PostMapping("/clear-history") // Updated mapping
    public ResponseEntity<Map<String, String>> clearChatHistory(@RequestBody Map<String, String> payload) {
        // --- Get userId from the request body ---
         Long userId = null;
        String userIdStr = payload.get("userId");
        if (userIdStr != null) {
            try {
                userId = Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                // Handle the case where userId is not a valid number
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid userId format."));
            }
        } else {
             // Handle the case where userId is missing from the body
            return ResponseEntity.badRequest().body(Map.of("error", "userId is required in the request body."));
        }
        // --- End of getting data from body ---


         // Use the userId to determine which history to clear
         String sessionIdForService = userId.toString(); // Using userId as session ID

        geminiService.clearHistory(sessionIdForService); // Use the derived sessionId
        return ResponseEntity.ok(Map.of("message", "Chat history cleared."));
    }
}