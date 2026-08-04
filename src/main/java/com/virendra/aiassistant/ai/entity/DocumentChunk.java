package com.virendra.aiassistant.ai.entity;

import com.virendra.aiassistant.document.entity.Document;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "document_chunks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer chunkIndex;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "chunk_embeddings",
            joinColumns = @JoinColumn(name = "chunk_id")
    )
    @Column(name = "value")
    private List<Float> embedding;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document document;

}