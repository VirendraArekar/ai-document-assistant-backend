package com.virendra.aiassistant.ai.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TextSplitter {

    private static final int CHUNK_SIZE = 1000;

    private static final int OVERLAP = 150;

    public List<String> split(String text) {

        List<String> chunks = new ArrayList<>();

        if (text == null || text.isBlank()) {
            return chunks;
        }

        int start = 0;

        while (start < text.length()) {

            int end = Math.min(start + CHUNK_SIZE, text.length());

            chunks.add(text.substring(start, end));

            start += CHUNK_SIZE - OVERLAP;
        }

        return chunks;
    }

}