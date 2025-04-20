package com.myfitnessbuddy.app.controller;

import com.myfitnessbuddy.app.service.*;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class ChatController {

    private final GeminiService geminiService;

    @Autowired
    public ChatController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("ai/chat/send")
    public Mono<ResponseEntity<Map<String, String>>> sendMessage(
            @RequestParam String sessionId,
            @RequestBody Map<String, String> payload,
            HttpSession session) {
            //System.out.println("SESSIONID: " + sessionId);
            Long userId = (Long) session.getAttribute("userId");
            //System.out.println("USERD ID: " + userId);
        String userMessage = payload.get("message");
        if (userMessage != null && !userMessage.trim().isEmpty()) {
            return geminiService.sendMessage(sessionId, userMessage, userId)
                    .map(aiResponse -> ResponseEntity.ok(Map.of("response", aiResponse)));
        } else {
            return Mono.just(ResponseEntity.badRequest().body(Map.of("error", "Message cannot be empty.")));
        }
    }

    @PostMapping("/ai/chat/clear-history")
    public ResponseEntity<Map<String, String>> clearChatHistory(@RequestParam String sessionId) {
        geminiService.clearHistory(sessionId);
        return ResponseEntity.ok(Map.of("message", "Chat history cleared."));
    }
}