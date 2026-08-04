package com.virendra.aiassistant.ai.controller;

import com.virendra.aiassistant.ai.dto.ChatRequest;
import com.virendra.aiassistant.ai.dto.ChatResponse;
import com.virendra.aiassistant.ai.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(
            @RequestBody ChatRequest request
    ) {

        return ResponseEntity.ok(
                aiService.ask(request.getMessage())
        );

    }

}