package com.bamis.banking_access.controller;

import com.bamis.banking_access.entity.BankingRequest;
import com.bamis.banking_access.entity.Justificatif;
import com.bamis.banking_access.repository.JustificatifRepository;
import com.bamis.banking_access.service.BankingRequestService;
import com.bamis.banking_access.service.JustificatifService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.bamis.banking_access.repository.ClientInfoRepository;
import com.bamis.banking_access.repository.BankingRequestRepository;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/banking-requests")
@CrossOrigin(origins = {"http://localhost:4202", "http://localhost:4200", "http://172.24.1.20:8080"})
@RequiredArgsConstructor
public class BankingRequestController {

    private final BankingRequestService bankingRequestService;
    private final ClientInfoRepository clientInfoRepository;
    private final BankingRequestRepository bankingRequestRepository;
    private final JustificatifService justificatifService;
    private static final Logger logger = LoggerFactory.getLogger(BankingRequestController.class);
    private final JustificatifRepository justificatifRepository;

    // -------------------------------------------------------------------------
    // ENDPOINT — Recuperer toutes les demandes
    // GET /api/banking-requests/all
    // -------------------------------------------------------------------------

    @GetMapping("/all")
    public ResponseEntity<?> getAllRequests() {
        try {
            logger.info("Recuperation de toutes les demandes banking");

            List<BankingRequest> requests = bankingRequestService.getAllRequests();

            List<Map<String, Object>> enrichedRequests = requests.stream()
                    .map(request -> {
                        Map<String, Object> data = new HashMap<>();
                        data.put("id", request.getId());
                        data.put("phoneNumber", request.getPhoneNumber());
                        data.put("clientName", request.getClientName());
                        data.put("email", request.getEmail());
                        data.put("serviceType", request.getServiceType());
                        data.put("modificationType", request.getModificationType());
                        data.put("otherMessage", request.getOtherMessage());
                        data.put("status", request.getStatus());
                        data.put("createdDate", request.getCreatedDate());
                        data.put("updatedDate", request.getUpdatedDate());
                        data.put("reference", request.getReference());
                        data.put("agence", request.getAgence());
                        data.put("custIden", request.getCode());
                        data.put("status_create", request.getStatus_create());
                        data.put("status_notif_last_date", request.getStatus_notif_last_date());



                        clientInfoRepository.findByPhoneNumber(request.getPhoneNumber())
                                .ifPresent(client -> {
//                                    data.put("custIden", client.getCustIden());
                                    data.put("emailSys", client.getEmail());
                                    data.put("firstName", client.getFirstName());
                                    data.put("lastName", client.getLastName());
//                                    data.put("agence", client.getWalletCode().substring(0, 5));
                                });

                        return data;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("total", enrichedRequests.size());
            response.put("data", enrichedRequests);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Erreur lors de la recuperation des demandes", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Erreur lors de la recuperation des demandes");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/agence/{agence}")
    public ResponseEntity<?> getRequestsByAgence(@PathVariable String agence) {
        try {
            List<BankingRequest> requests = bankingRequestService.getAllRequests();

            List<Map<String, Object>> enrichedRequests = requests.stream()
                    .map(request -> {
                        Map<String, Object> data = new HashMap<>();
                        data.put("id", request.getId());
                        data.put("phoneNumber", request.getPhoneNumber());
                        data.put("clientName", request.getClientName());
                        data.put("email", request.getEmail());
                        data.put("serviceType", request.getServiceType());
                        data.put("modificationType", request.getModificationType());
                        data.put("otherMessage", request.getOtherMessage());
                        data.put("status", request.getStatus());
                        data.put("createdDate", request.getCreatedDate());
                        data.put("updatedDate", request.getUpdatedDate());
                        data.put("reference", request.getReference());
                        data.put("agence", request.getAgence());
                        data.put("custIden", request.getCode());
                        data.put("status_create", request.getStatus_create());
                        data.put("status_notif_last_date", request.getStatus_notif_last_date());

                        clientInfoRepository.findByPhoneNumber(request.getPhoneNumber())
                                .ifPresent(client -> {
                                    data.put("custIden", client.getCustIden());
                                    data.put("emailSys", client.getEmail());
                                    data.put("firstName", client.getFirstName());
                                    data.put("lastName", client.getLastName());
//                                    data.put("agence", client.getWalletCode().substring(0, 5));
                                });

                        return data;
                    })
                    .filter(data -> agence.equals(data.get("agence")))  // filtre ici
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("agence", agence);
            response.put("total", enrichedRequests.size());
            response.put("data", enrichedRequests);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Erreur lors de la recuperation des demandes pour agence={}", agence, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Erreur lors de la recuperation des demandes");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // -------------------------------------------------------------------------
    // ENDPOINT — Recuperer par statut
    // GET /api/banking-requests/status/{status}
    // -------------------------------------------------------------------------

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getRequestsByStatus(@PathVariable String status) {
        try {
            logger.info("Recuperation des demandes avec statut: {}", status);

            List<BankingRequest> requests = bankingRequestService.getRequestsByStatus(status);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("total", requests.size());
            response.put("data", requests);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Erreur lors de la recuperation des demandes par statut: {}", status, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Erreur lors de la recuperation des demandes");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // -------------------------------------------------------------------------
    // ENDPOINT — Upload de justificatifs
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
            logger.warn("Argument invalide ou demande introuvable pour id={} : {}", id, e.getMessage());
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
    // ENDPOINT — Cloture de la demande
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

            //  Vérification du statut avant clôture
            if ("PENDING".equalsIgnoreCase(bankingRequest.getStatus())) {
                response.put("success", false);
                response.put("message", "Impossible de clôturer la demande : elle est toujours en attente de traitement");
                response.put("status", bankingRequest.getStatus());
                logger.warn("Tentative de cloture refusee - demande id={} toujours en statut PENDING", id);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            bankingRequest.setStatus("PROCESSED");
            bankingRequest.setUpdatedDate(LocalDateTime.now());
            bankingRequestRepository.save(bankingRequest);

            response.put("success", true);
            response.put("message", "Demande cloturee avec succes");
            response.put("id", id);
            response.put("status", "PROCESSED");

            logger.info("Demande id={} cloturee avec succes", id);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Erreur lors de la cloture de la demande id={}", id, e);
            response.put("success", false);
            response.put("message", "Erreur lors de la cloture de la demande : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // -------------------------------------------------------------------------
// ENDPOINT — Récupérer la liste des justificatifs d'une demande
// GET /api/banking-requests/{id}/justificatifs
// -------------------------------------------------------------------------

    @GetMapping("/{id}/justificatifs")
    public ResponseEntity<Map<String, Object>> getJustificatifs(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            System.out.println("=== GET JUSTIFICATIFS - id=" + id + " ===");

            Optional<BankingRequest> optionalRequest = bankingRequestRepository.findById(id);
            if (!optionalRequest.isPresent()) {
                System.out.println("❌ Demande introuvable pour id=" + id);
                response.put("message", "Demande introuvable");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            System.out.println("✅ Demande trouvée: " + optionalRequest.get().getReference());

            List<Justificatif> justificatifs = justificatifRepository.findByBankingRequestId(id);
            System.out.println("📁 Nombre de justificatifs trouvés en BDD: " + justificatifs.size());

            if (justificatifs.isEmpty()) {
                System.out.println("⚠️ Aucun justificatif en base pour id=" + id);
                System.out.println("   → Vérifier la table 'justificatifs' pour banking_request_id=" + id);
            }

            for (Justificatif j : justificatifs) {
                System.out.println("  - Fichier: " + j.getFileName() + " | path: " + j.getFilePath() + " | size: " + j.getFileSize());
            }

            List<Map<String, Object>> files = justificatifs.stream()
                    .map(j -> {
                        Map<String, Object> file = new HashMap<>();
                        file.put("id", j.getId());
                        file.put("fileName", j.getFileName());
                        file.put("fileSize", j.getFileSize());
                        file.put("uploadedAt", j.getUploadedAt());
                        return file;
                    })
                    .collect(Collectors.toList());

            response.put("success", true);
            response.put("data", files);
            System.out.println("=== FIN GET JUSTIFICATIFS ===");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("❌ ERREUR getJustificatifs id=" + id + " : " + e.getMessage());
            e.printStackTrace();
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

// -------------------------------------------------------------------------
// ENDPOINT — Télécharger / afficher un justificatif
// GET /api/banking-requests/{id}/justificatifs/{fileName}
// -------------------------------------------------------------------------

    @GetMapping("/{id}/justificatifs/{fileName}")
    public ResponseEntity<org.springframework.core.io.Resource> downloadJustificatif(
            @PathVariable Long id,
            @PathVariable String fileName) {
        try {
            List<Justificatif> justificatifs = justificatifRepository.findByBankingRequestId(id);

            Justificatif justificatif = justificatifs.stream()
                    .filter(j -> j.getFileName().equals(fileName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Fichier introuvable : " + fileName));

            java.nio.file.Path filePath = java.nio.file.Paths.get(justificatif.getFilePath());
            org.springframework.core.io.Resource resource =
                    new org.springframework.core.io.UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            String contentType = determineContentType(fileName);

            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + fileName + "\"")
                    .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (Exception e) {
            logger.error("Erreur lors du telechargement du fichier {} pour id={}", fileName, id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String determineContentType(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".pdf"))  return "application/pdf";
        if (lower.endsWith(".png"))  return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".gif"))  return "image/gif";
        if (lower.endsWith(".webp")) return "image/webp";
        return "application/octet-stream";
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitRequest(@RequestBody Map<String, Object> requestBody) {
        try {
            String code = getStr(requestBody, "code");
            String requesterType = getStr(requestBody, "requesterType");

            // Vérification existence du code dans epargne_client_info
            if (code.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Le code client / code société est obligatoire");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            boolean codeExists = clientInfoRepository.findById(code).isPresent();
            if (!codeExists) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Le code \"" + code + "\" n'existe pas dans le système. Veuillez vérifier le code saisi.");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            BankingRequest request = BankingRequest.builder()
                    .phoneNumber(getStr(requestBody, "phoneNumber"))
                    .clientName(getStr(requestBody, "clientName"))
                    .email(getStr(requestBody, "email"))
                    .serviceType(getStr(requestBody, "serviceType"))
                    .modificationType(getStr(requestBody, "modificationType"))
                    .otherMessage(getStr(requestBody, "otherMessage"))
                    .requesterType(requesterType)
                    .code(code)
                    .agence(getStr(requestBody, "agence"))
                    .createdByLogin(getStr(requestBody, "createdByLogin"))
                    .status("PENDING")
                    .build();

            BankingRequest saved = bankingRequestService.createRequestByGestionnaire(request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Demande créée avec succès");
            response.put("requestId", saved.getId());
            response.put("reference", saved.getReference());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Erreur création demande gestionnaire", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Erreur: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // méthode utilitaire privée à ajouter dans le controller
    private String getStr(Map<String, Object> body, String key) {
        Object val = body.get(key);
        return val != null ? val.toString().trim() : "";
    }


    @GetMapping("/by-code/{code}")
    public ResponseEntity<?> getRequestsByCode(@PathVariable String code) {
        try {
            logger.info("Recuperation des demandes pour le code: {}", code);

            List<BankingRequest> requests = bankingRequestRepository.findByCodeOrderByCreatedDateDesc(code);

            List<Map<String, Object>> enrichedRequests = requests.stream()
                    .map(request -> {
                        Map<String, Object> data = new HashMap<>();
                        data.put("id", request.getId());
                        data.put("phoneNumber", request.getPhoneNumber());
                        data.put("clientName", request.getClientName());
                        data.put("email", request.getEmail());
                        data.put("serviceType", request.getServiceType());
                        data.put("modificationType", request.getModificationType());
                        data.put("otherMessage", request.getOtherMessage());
                        data.put("status", request.getStatus());
                        data.put("createdDate", request.getCreatedDate());
                        data.put("updatedDate", request.getUpdatedDate());
                        data.put("reference", request.getReference());
                        data.put("code", request.getCode());
                        data.put("agence", request.getAgence());
                        data.put("requesterType", request.getRequesterType());
                        data.put("createdByLogin", request.getCreatedByLogin());

                        clientInfoRepository.findById(request.getCode())
                                .ifPresent(client -> {
                                    data.put("custIden", client.getCustIden());
                                    data.put("emailSys", client.getEmail());
                                    data.put("firstName", client.getFirstName());
                                    data.put("lastName", client.getLastName());
                                    data.put("agence", client.getWalletCode() != null && client.getWalletCode().length() >= 5
                                            ? client.getWalletCode().substring(0, 5) : client.getWalletCode());
                                });

                        return data;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("total", enrichedRequests.size());
            response.put("data", enrichedRequests);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Erreur lors de la recuperation des demandes pour le code: {}", code, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Erreur lors de la recuperation des demandes");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @GetMapping("/phone/{phoneNumber}/has-pending")
    public ResponseEntity<?> checkPendingRequest(@PathVariable String phoneNumber) {
        boolean hasPending = bankingRequestService.hasPendingRequest(phoneNumber);

        Map<String, Object> response = new HashMap<>();
        response.put("hasPendingRequest", hasPending);

        if (hasPending) {
            // Récupérer la demande pour afficher sa référence au client
            bankingRequestService.getRequestsByPhoneNumber(phoneNumber)
                    .stream()
                    .filter(r -> "PENDING".equals(r.getStatus()))
                    .findFirst()
                    .ifPresent(r -> {
                        response.put("reference", r.getReference());
                        response.put("createdDate", r.getCreatedDate());
                        response.put("message",
                                "Vous avez déjà une demande en attente (Réf: " + r.getReference() + "). " +
                                        "Veuillez attendre sa clôture avant d'en soumettre une nouvelle."
                        );
                    });
        }

        return ResponseEntity.ok(response);
    }


    @PostMapping("/sync-and-update")
    public ResponseEntity<Map<String, Object>> syncAndUpdate() {
        try {
            System.out.println("Déclenchement sync-and-update");
            Map<String, Object> result = bankingRequestService.syncAndUpdate();

            if (Boolean.TRUE.equals(result.get("success"))) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(result);
            }

        } catch (Exception e) {
            System.out.println("Erreur lors du sync-and-update"+ e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Erreur interne : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


}


