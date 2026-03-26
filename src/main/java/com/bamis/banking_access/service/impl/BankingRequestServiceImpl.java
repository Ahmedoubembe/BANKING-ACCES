package com.bamis.banking_access.service.impl;

import com.bamis.banking_access.entity.BankingRequest;
import com.bamis.banking_access.repository.BankingRequestRepository;
import com.bamis.banking_access.service.BankingRequestService;
//import com.sun.org.slf4j.internal.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class BankingRequestServiceImpl implements BankingRequestService {

    private final BankingRequestRepository bankingRequestRepository;

    @Override
    @Transactional
    public BankingRequest createRequest(BankingRequest request) {
        if (request.getStatus() == null) {
            request.setStatus("PENDING");
        }
        return bankingRequestRepository.save(request);
    }


    @Override
    @Transactional(readOnly = true)
    public List<BankingRequest> getAllRequests() {
        return bankingRequestRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankingRequest> getRequestsByPhoneNumber(String phoneNumber) {
        return bankingRequestRepository.findByPhoneNumberOrderByCreatedDateDesc(phoneNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankingRequest> getRequestsByStatus(String status) {
        return bankingRequestRepository.findByStatusOrderByCreatedDateDesc(status);
    }

    @Override
    @Transactional
    public BankingRequest createRequestByGestionnaire(BankingRequest request) {
        if (request.getStatus() == null) {
            request.setStatus("PENDING");
        }
        if (request.getPhoneNumber() == null) {
            request.setPhoneNumber("");
        }
        return bankingRequestRepository.save(request);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPendingRequest(String phoneNumber) {
        return bankingRequestRepository.existsByPhoneNumberAndStatus(phoneNumber, "PENDING");
    }


    private static final String SYNC_URL =
            "http://172.25.25.18/AutresServices/api/sync/clients?dateDebut={dateDebut}&dateFin={dateFin}";

    @Override
    @Transactional
    public Map<String, Object> syncAndUpdate() {
        Map<String, Object> result = new HashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateDebut = LocalDate.now().format(formatter);
        String dateFin   = LocalDate.now().plusDays(5).format(formatter);

        System.out.println("Lancement sync clients : dateDebut=" + dateDebut + " dateFin=" + dateFin);
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> syncResponse = restTemplate.postForEntity(
                    SYNC_URL, null, String.class, dateDebut, dateFin
            );

            System.out.println("Réponse sync : HTTP " + syncResponse.getStatusCode().value());

            if (!syncResponse.getStatusCode().is2xxSuccessful()) {
                result.put("success", false);
                result.put("message", "L'API de sync a retourné un statut non-OK : " + syncResponse.getStatusCode());
                return result;
            }

        } catch (Exception e) {
            System.out.println("Erreur lors de l'appel à l'API de sync"+ e);
            result.put("success", false);
            result.put("message", "Erreur lors de l'appel à l'API de sync : " + e.getMessage());
            return result;
        }

        int agencesUpdated = bankingRequestRepository.updateAgenceFromClientInfo();
        int codesUpdated   = bankingRequestRepository.updateCodeFromClientInfo();

        System.out.println("agence mis à jour : {} ligne(s), code mis à jour : {} ligne(s)"+agencesUpdated+ codesUpdated);
        result.put("success", true);
        result.put("message", "Synchronisation et mise à jour effectuées avec succès");
        result.put("dateDebut", dateDebut);
        result.put("dateFin", dateFin);
        result.put("agencesUpdated", agencesUpdated);
        result.put("codesUpdated", codesUpdated);

        return result;
    }
}