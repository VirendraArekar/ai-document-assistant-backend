package com.virendra.aiassistant.ai.service;

import com.virendra.aiassistant.ai.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AIService {

    private final RagService ragService;

    public ChatResponse ask(String question) {

        return ChatResponse.builder()
                .answer(
                        ragService.ask(question)
                )
                .build();

    }

}