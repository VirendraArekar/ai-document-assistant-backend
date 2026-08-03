package com.virendra.aiassistant.ai.service;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class TikaService {

    private final Tika tika = new Tika();

    public String extractText(String filePath) {

        try {

            return tika.parseToString(new File(filePath));

        } catch (IOException | TikaException e) {

            throw new RuntimeException("Unable to extract text", e);

        }

    }

}