package com.bamis.banking_access.service.impl;

import com.bamis.banking_access.entity.BankingRequest;
import com.bamis.banking_access.repository.BankingRequestRepository;
import com.bamis.banking_access.service.BankingRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BankingRequestServiceImpl implements BankingRequestService {

    private final BankingRequestRepository bankingRequestRepository;

    @Override
    @Transactional
    public BankingRequest createRequest(BankingRequest request) {
        if (request.getStatus() == null) {
            request.setStatus("VALIDATED");
        }
        return bankingRequestRepository.save(request);
    }
}