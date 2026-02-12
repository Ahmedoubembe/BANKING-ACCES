package com.bamis.banking_access.service;

import com.bamis.banking_access.entity.BankingRequest;

public interface BankingRequestService {

    /**
     * Crée une nouvelle demande d'accès banking
     */
    BankingRequest createRequest(BankingRequest request);

}
