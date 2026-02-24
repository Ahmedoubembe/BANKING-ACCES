package com.bamis.banking_access.controller;

import com.bamis.banking_access.entity.BankingRequest;
import com.bamis.banking_access.repository.BankingRequestRepository;
import com.bamis.banking_access.service.JustificatifService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/banking-requests")
@CrossOrigin(origins = {"http://localhost:4202", "http://localhost:4200", "http://172.24.1.20:8080"})
@RequiredArgsConstructor
public class JustificatifController {

    private static final Logger logger = LoggerFactory.getLogger(JustificatifController.class);

    private final JustificatifService justificatifService;
    private final BankingRequestRepository bankingRequestRepository;

    // -------------------------------------------------------------------------
    // ENDPOINT 1 — Upload de justificatifs
    // POST /api/banking-requests/{id}/justificatifs
    // -------------------------------------------------------------------------

    @PostMapping("/{id}/justificatifs")
    public ResponseEntity<Map<String, Object>> uploadJustificatifs(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files) {

        Map<String, Object> response = new HashMap<>();

        try {
            logger.info("Upload de justificatifs pour la demande id={}", id);

            List<String> uploadedFiles = justificatifService.uploadFiles(id, files);

            response.put("message", "Fichiers uploades avec succes");
            response.put("uploadedFiles", uploadedFiles);

            logger.info("Upload reussi pour la demande id={} - {} fichier(s)", id, uploadedFiles.size());
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.warn("Demande introuvable ou argument invalide pour id={} : {}", id, e.getMessage());
            response.put("message", e.getMessage());
            HttpStatus status = e.getMessage().contains("introuvable") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(response);

        } catch (Exception e) {
            logger.error("Erreur lors de l'upload des justificatifs pour id={}", id, e);
            response.put("message", "Erreur lors de l'upload des fichiers : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // -------------------------------------------------------------------------
    // ENDPOINT 2 — Cloture de la demande
    // PATCH /api/banking-requests/{id}/close
    // -------------------------------------------------------------------------

    @PatchMapping("/{id}/close")
    public ResponseEntity<Map<String, Object>> closeRequest(@PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();

        try {
            logger.info("Cloture de la demande id={}", id);

            Optional<BankingRequest> optionalRequest = bankingRequestRepository.findById(id);

            if (!optionalRequest.isPresent()) {
                response.put("message", "Demande introuvable");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            BankingRequest bankingRequest = optionalRequest.get();
            bankingRequest.setStatus("PROCESSED");
            bankingRequest.setUpdatedDate(LocalDateTime.now());
            bankingRequestRepository.save(bankingRequest);

            response.put("message", "Demande cloturee avec succes");
            response.put("id", id);
            response.put("status", "PROCESSED");

            logger.info("Demande id={} cloturee avec succes", id);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Erreur lors de la cloture de la demande id={}", id, e);
            response.put("message", "Erreur lors de la cloture de la demande : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}