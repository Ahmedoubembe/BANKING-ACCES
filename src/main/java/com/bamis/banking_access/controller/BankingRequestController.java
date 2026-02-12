package com.bamis.banking_access.controller;

import com.bamis.banking_access.entity.BankingRequest;
import com.bamis.banking_access.service.BankingRequestService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/banking-requests")
@CrossOrigin(origins = {"http://localhost:4202"})
@RequiredArgsConstructor
public class BankingRequestController {

    private final BankingRequestService bankingRequestService;
    private static final Logger logger = LoggerFactory.getLogger(BankingRequestController.class);

    /**
     * Endpoint pour soumettre une demande d'accès banking après validation OTP
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitRequest(@RequestBody Map<String, Object> requestBody) {
        try {
            // Récupération des données
            String phoneNumber = (String) requestBody.get("phoneNumber");
            String clientName = (String) requestBody.get("clientName");
            String email = (String) requestBody.get("email");
            String serviceType = (String) requestBody.get("serviceType");
            String modificationType = (String) requestBody.get("modificationType");
            String otherMessage = (String) requestBody.get("otherMessage");

            // Validation des champs obligatoires
            if (phoneNumber == null || phoneNumber.trim().isEmpty() ||
                    clientName == null || clientName.trim().isEmpty() ||
                    email == null || email.trim().isEmpty() ||
                    serviceType == null || serviceType.trim().isEmpty() ||
                    modificationType == null || modificationType.trim().isEmpty()) {

                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Tous les champs obligatoires doivent être renseignés");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            logger.info("Soumission demande banking pour: {} - Service: {} - Type: {}",
                    phoneNumber, serviceType, modificationType);

            // Créer l'entité
            BankingRequest request = BankingRequest.builder()
                    .phoneNumber(phoneNumber.trim())
                    .clientName(clientName.trim())
                    .email(email.trim())
                    .serviceType(serviceType.trim())
                    .modificationType(modificationType.trim())
                    .otherMessage(otherMessage != null ? otherMessage.trim() : null)
                    .status("VALIDATED")
                    .build();

            // Sauvegarder
            BankingRequest savedRequest = bankingRequestService.createRequest(request);

            // Réponse succès
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Votre demande a été soumise avec succès");
            response.put("requestId", savedRequest.getId());

            logger.info("Demande banking créée avec succès - ID: {}", savedRequest.getId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Erreur lors de la soumission de la demande banking", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Erreur lors de la soumission de la demande: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}