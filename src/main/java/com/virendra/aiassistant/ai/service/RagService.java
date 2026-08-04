package com.virendra.aiassistant.ai.service;

import com.virendra.aiassistant.ai.dto.SimilarityResult;
import dev.langchain4j.model.chat.ChatModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RagService {

    private final SimilarityService similarityService;

    private final ChatModel chatModel;

    public String ask(String question) {

        List<SimilarityResult> chunks =

                similarityService.search(question);

        StringBuilder context =

                new StringBuilder();

        for (SimilarityResult chunk : chunks) {

            context.append(chunk.getContent())

                    .append("\n\n");

        }

        String prompt = """

You are an AI assistant.

Answer ONLY using the provided context.

If the answer is unavailable, reply:

I couldn't find this information.

Context

%s

Question

%s

""".formatted(

                context,

                question

        );

        return chatModel.chat(prompt);

    }

}