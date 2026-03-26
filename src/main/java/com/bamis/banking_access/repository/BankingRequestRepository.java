package com.bamis.banking_access.repository;

import com.bamis.banking_access.entity.BankingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankingRequestRepository extends JpaRepository<BankingRequest, Long> {

    List<BankingRequest> findByPhoneNumberOrderByCreatedDateDesc(String phoneNumber);

    // Récupérer les demandes par statut
    List<BankingRequest> findByStatusOrderByCreatedDateDesc(String status);

    List<BankingRequest> findByCodeOrderByCreatedDateDesc(String code);

    boolean existsByPhoneNumberAndStatus(String phoneNumber, String status);
}
