package com.virendra.aiassistant.ai.service;

import com.virendra.aiassistant.ai.dto.SimilarityResult;
import com.virendra.aiassistant.ai.entity.DocumentChunk;
import com.virendra.aiassistant.ai.repository.DocumentChunkRepository;
import com.virendra.aiassistant.ai.util.CosineSimilarityUtil;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class SimilarityService {

    private final DocumentChunkRepository repository;

    private final EmbeddingModel embeddingModel;

    public List<SimilarityResult> search(

            String question

    ) {

        Embedding embedding =

                embeddingModel.embed(question).content();

        List<Float> questionVector = new ArrayList<>();
        for (float f : embedding.vector()) {
            questionVector.add(f);
        }

        List<SimilarityResult> results =

                new ArrayList<>();

        for (DocumentChunk chunk :

                repository.findAll()) {

            double score =

                    CosineSimilarityUtil.cosineSimilarity(

                            questionVector,

                            chunk.getEmbedding()

                    );

            results.add(

                    SimilarityResult.builder()

                            .content(chunk.getContent())

                            .score(score)

                            .build()

            );

        }

        results.sort(

                Comparator.comparing(

                        SimilarityResult::getScore

                ).reversed()

        );

        return results.stream()

                .limit(5)

                .toList();

    }

}