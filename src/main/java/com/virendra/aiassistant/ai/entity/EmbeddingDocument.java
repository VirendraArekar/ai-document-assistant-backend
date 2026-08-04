package com.virendra.aiassistant.ai.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="document_embeddings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddingDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long documentId;

    private Integer chunkIndex;

    @Column(columnDefinition="TEXT")
    private String content;

}