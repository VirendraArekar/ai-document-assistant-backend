package com.virendra.aiassistant.document.service;

import com.virendra.aiassistant.ai.service.EmbeddingService;
import com.virendra.aiassistant.ai.service.TikaService;
import com.virendra.aiassistant.auth.entity.User;
import com.virendra.aiassistant.auth.repository.UserRepository;
import com.virendra.aiassistant.document.dto.DocumentResponse;
import com.virendra.aiassistant.document.entity.Document;
import com.virendra.aiassistant.document.repository.DocumentRepository;
import com.virendra.aiassistant.document.util.FileStorageUtil;
import com.virendra.aiassistant.document.util.FileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final EmbeddingService embeddingService;
    private final FileStorageUtil fileStorageUtil;
    private final FileValidator fileValidator;
    private final TikaService tikaService;

    private String generatePreview(String extractedText) {
        if (extractedText == null || extractedText.isBlank()) {
            return "";
        }
        return extractedText.substring(0, Math.min(300, extractedText.length()));
    }

    public DocumentResponse upload(
            MultipartFile file,
            String email
    ) throws Exception {

        fileValidator.validate(file);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        String path = fileStorageUtil.save(file);

        String extractedText = tikaService.extractText(path);

        Document document = Document.builder()
                .fileName(file.getOriginalFilename())
                .originalFileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .filePath(path)
                .extractedText(extractedText)
                .user(user)
                .build();

        document = documentRepository.save(document);

        /*
         * Split the extracted text into chunks.
         * Later this method will generate embeddings
         * and store them in pgvector.
         */
        embeddingService.processDocument(document);

        return DocumentResponse.builder()
                .id(document.getId())
                .fileName(document.getFileName())
                .originalFileName(document.getOriginalFileName())
                .fileType(document.getFileType())
                .fileSize(document.getFileSize())
                .uploadedAt(document.getUploadedAt())
//                .preview(generatePreview(document.getExtractedText()))
                .preview(document.getExtractedText()==null ? "" :
                                document.getExtractedText()
                                        .substring(0,Math.min(300,document.getExtractedText().length())))
                .build();
    }

    public Page<DocumentResponse> getDocuments(

            String email,

            int page,

            int size,

            String search

    ) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Document> documents;

        if (search == null || search.isBlank()) {

            documents = documentRepository.findByUser_Email(
                    email,
                    pageable
            );

        } else {

            documents =
                    documentRepository.findByUser_EmailAndOriginalFileNameContainingIgnoreCase(

                            email,

                            search,

                            pageable

                    );

        }

        return documents.map(document ->

                DocumentResponse.builder()

                        .id(document.getId())

                        .fileName(document.getFileName())

                        .originalFileName(document.getOriginalFileName())

                        .fileType(document.getFileType())

                        .fileSize(document.getFileSize())

                        .uploadedAt(document.getUploadedAt())

                        .preview(generatePreview(document.getExtractedText()))

                        .build()

        );

    }

    public Resource download(

            Long id,

            String email

    ) throws Exception {

        Document document =

                documentRepository.findById(id)

                        .orElseThrow(() ->

                                new RuntimeException("Document not found")

                        );

        if (!document.getUser().getEmail().equals(email)) {

            throw new RuntimeException("Access denied");

        }

        Path path = Paths.get(document.getFilePath());

        return new UrlResource(path.toUri());

    }


    public void delete(

            Long id,

            String email

    ) {

        Document document =

                documentRepository.findById(id)

                        .orElseThrow(() ->

                                new RuntimeException("Document not found")

                        );

        if (!document.getUser().getEmail().equals(email)) {

            throw new RuntimeException("Access denied");

        }

        File file = new File(document.getFilePath());

        if (file.exists()) {

            file.delete();

        }

        documentRepository.delete(document);

    }

    public DocumentResponse getById(

            Long id,

            String email

    ) {

        Document document =

                documentRepository.findById(id)

                        .orElseThrow(() ->

                                new RuntimeException("Document not found")

                        );

        if (!document.getUser().getEmail().equals(email)) {

            throw new RuntimeException("Access denied");

        }

        return DocumentResponse.builder()

                .id(document.getId())

                .fileName(document.getFileName())

                .originalFileName(document.getOriginalFileName())

                .fileType(document.getFileType())

                .fileSize(document.getFileSize())

                .uploadedAt(document.getUploadedAt())

                .preview(generatePreview(document.getExtractedText()))

                .build();

    }

    public String getDocumentText(

            Long id,

            String email

    ){

        Document document=

                documentRepository.findById(id)

                        .orElseThrow(()->

                                new RuntimeException("Document not found"));

        if(!document.getUser().getEmail().equals(email))

            throw new RuntimeException("Unauthorized");

        return document.getExtractedText();

    }
}