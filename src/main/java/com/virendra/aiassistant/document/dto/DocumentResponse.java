package com.virendra.aiassistant.document.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {

    private Long id;

    private String fileName;

    private String originalFileName;

    private String fileType;

    private Long fileSize;

    private LocalDateTime uploadedAt;

}