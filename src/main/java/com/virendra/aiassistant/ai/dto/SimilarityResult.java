package com.virendra.aiassistant.ai.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimilarityResult {

    private String content;

    private Double score;

}