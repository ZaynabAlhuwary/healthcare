package com.healthcare.healthcare_system.controller;

import com.healthcare.healthcare_system.dto.ChatRequest;
import com.healthcare.healthcare_system.dto.ChatResponse;
import com.healthcare.healthcare_system.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Ai controller.
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class AIController {
    private final AIService aiService;

    /**
     * Process query response entity.
     *
     * @param chatRequest the chat request
     * @return the response entity
     */
    @PostMapping
    public ResponseEntity<ChatResponse> processQuery(@RequestBody ChatRequest chatRequest) {
        return ResponseEntity.ok(aiService.processQuery(chatRequest));
    }
}
