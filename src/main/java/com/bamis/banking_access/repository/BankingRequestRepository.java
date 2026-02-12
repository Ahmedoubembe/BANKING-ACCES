package com.bamis.banking_access.repository;

import com.bamis.banking_access.entity.BankingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankingRequestRepository extends JpaRepository<BankingRequest, Long> {

    // Pas de méthodes supplémentaires pour le moment
    // Les méthodes de base (save, findById, findAll, delete) sont héritées de JpaRepository

}
