package com.bamis.banking_access.service;

import com.bamis.banking_access.entity.BankingRequest;

import java.util.List;

public interface BankingRequestService {

    /**
     * Crée une nouvelle demande d'accès banking
     */
    BankingRequest createRequest(BankingRequest request);

    /**
     * Récupère toutes les demandes
     */
    List<BankingRequest> getAllRequests();

    /**
     * Récupère les demandes d'un client par numéro de téléphone
     */
    List<BankingRequest> getRequestsByPhoneNumber(String phoneNumber);

    /**
     * Récupère les demandes par statut
     */
    List<BankingRequest> getRequestsByStatus(String status);

    BankingRequest createRequestByGestionnaire(BankingRequest request);

    boolean hasPendingRequest(String phoneNumber);
}
