package com.bamis.banking_access.service;

import com.bamis.banking_access.entity.BankingRequest;
import com.bamis.banking_access.entity.Justificatif;
import com.bamis.banking_access.repository.BankingRequestRepository;
import com.bamis.banking_access.repository.JustificatifRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JustificatifService {

    private final JustificatifRepository justificatifRepository;
    private final BankingRequestRepository bankingRequestRepository;

    @Value("${app.upload.dir:uploads/justificatifs}")
    private String uploadBaseDir;

    @Transactional
    public List<String> uploadFiles(Long requestId, List<MultipartFile> files) throws IOException {

        BankingRequest bankingRequest = bankingRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Demande introuvable avec l'id : " + requestId));

        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("Aucun fichier fourni");
        }

        Path uploadDir = Paths.get(uploadBaseDir, String.valueOf(requestId));
        Files.createDirectories(uploadDir);

        List<String> uploadedFileNames = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Un fichier fourni est vide : " + file.getOriginalFilename());
            }

            String originalFileName = file.getOriginalFilename();
            Path destinationPath = uploadDir.resolve(originalFileName);
//            file.transferTo(destinationPath.toFile());
            Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            Justificatif justificatif = Justificatif.builder()
                    .bankingRequest(bankingRequest)
                    .fileName(originalFileName)
                    .filePath(destinationPath.toString())
                    .fileSize(file.getSize())
                    .build();

            justificatifRepository.save(justificatif);
            uploadedFileNames.add(originalFileName);
        }

        return uploadedFileNames;
    }
}