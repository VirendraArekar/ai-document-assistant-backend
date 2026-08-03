package com.virendra.aiassistant.document.repository;

import com.virendra.aiassistant.document.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    Page<Document> findByUser_Email(
            String email,
            Pageable pageable
    );

    Page<Document> findByUser_EmailAndOriginalFileNameContainingIgnoreCase(
            String email,
            String keyword,
            Pageable pageable
    );

}