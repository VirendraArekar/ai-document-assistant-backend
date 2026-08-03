package com.virendra.aiassistant.document.controller;

import com.virendra.aiassistant.document.dto.DocumentResponse;
import com.virendra.aiassistant.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<DocumentResponse> upload(

            @RequestParam("file")
            MultipartFile file,

            Authentication authentication

    ) throws Exception {

        return ResponseEntity.ok(

                documentService.upload(
                        file,
                        authentication.getName()
                )

        );

    }

    @GetMapping
    public ResponseEntity<Page<DocumentResponse>> getDocuments(

            Authentication authentication,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "")
            String search

    ) {

        return ResponseEntity.ok(

                documentService.getDocuments(

                        authentication.getName(),

                        page,

                        size,

                        search

                )

        );

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(

            @PathVariable Long id,

            Authentication authentication

    ) {

        documentService.delete(

                id,

                authentication.getName()

        );

        return ResponseEntity.ok(

                "Document deleted successfully"

        );

    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocument(

            @PathVariable Long id,

            Authentication authentication

    ) {

        return ResponseEntity.ok(

                documentService.getById(

                        id,

                        authentication.getName()

                )

        );

    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(

            @PathVariable Long id,

            Authentication authentication

    ) throws Exception {

        Resource resource =

                documentService.download(

                        id,

                        authentication.getName()

                );

        return ResponseEntity.ok()

                .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")

                .header(

                        HttpHeaders.CONTENT_DISPOSITION,

                        "attachment; filename=\"" +

                                resource.getFilename()

                                + "\""

                )

                .body(resource);

    }
}