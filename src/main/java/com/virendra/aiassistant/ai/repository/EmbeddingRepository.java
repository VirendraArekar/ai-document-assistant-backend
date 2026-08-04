package com.virendra.aiassistant.ai.repository;

import com.virendra.aiassistant.ai.entity.EmbeddingDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmbeddingRepository
        extends JpaRepository<EmbeddingDocument,Long> {

}