package com.bamis.banking_access.repository;

import com.bamis.banking_access.entity.Justificatif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JustificatifRepository extends JpaRepository<Justificatif, Long> {

    List<Justificatif> findByBankingRequestId(Long bankingRequestId);
}