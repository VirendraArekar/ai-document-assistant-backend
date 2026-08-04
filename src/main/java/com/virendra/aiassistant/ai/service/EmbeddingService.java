package com.virendra.aiassistant.ai.service;

import com.virendra.aiassistant.ai.entity.DocumentChunk;
import com.virendra.aiassistant.ai.repository.DocumentChunkRepository;
import com.virendra.aiassistant.document.entity.Document;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final TextSplitter splitter;

    private final DocumentChunkRepository repository;

    private final EmbeddingModel embeddingModel;

    public void processDocument(Document document) {

        List<String> chunks =
                splitter.split(document.getExtractedText());

        int index = 0;

        for (String chunk : chunks) {

            Embedding embedding =
                    embeddingModel.embed(chunk).content();

            List<Float> embeddingList = new ArrayList<>();
            for (float f : embedding.vector()) {
                embeddingList.add(f);
            }

            DocumentChunk entity =
                    DocumentChunk.builder()
                            .chunkIndex(index++)
                            .content(chunk)
                            .embedding(embeddingList)
                            .document(document)
                            .build();

            repository.save(entity);

        }

    }

}