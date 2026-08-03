package com.virendra.aiassistant.document.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class FileValidator {

    private static final List<String> ALLOWED_TYPES = List.of(
            "application/pdf",
            "text/plain",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private static final long MAX_SIZE =
            20 * 1024 * 1024;

    public void validate(MultipartFile file) {

        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new RuntimeException("Invalid file type");
        }

        if (file.getSize() > MAX_SIZE) {
            throw new RuntimeException("Maximum size is 20 MB");
        }

    }

}