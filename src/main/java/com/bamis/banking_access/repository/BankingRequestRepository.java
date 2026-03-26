package com.bamis.banking_access.repository;

import com.bamis.banking_access.entity.BankingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankingRequestRepository extends JpaRepository<BankingRequest, Long> {

    List<BankingRequest> findByPhoneNumberOrderByCreatedDateDesc(String phoneNumber);

    // Récupérer les demandes par statut
    List<BankingRequest> findByStatusOrderByCreatedDateDesc(String status);

    List<BankingRequest> findByCodeOrderByCreatedDateDesc(String code);

    boolean existsByPhoneNumberAndStatus(String phoneNumber, String status);

    @Modifying
    @Query(value = "UPDATE banking_requests AS re " +
            "JOIN epargne_client_info AS inf ON inf.phone_number = re.phone_number " +
            "SET re.agence = LEFT(inf.wallet_code, 5) " +
            "WHERE re.agence IS NULL", nativeQuery = true)
    int updateAgenceFromClientInfo();

    @Modifying
    @Query(value = "UPDATE banking_requests AS re " +
            "JOIN epargne_client_info AS inf ON inf.phone_number = re.phone_number " +
            "SET re.code = inf.cust_iden " +
            "WHERE re.code IS NULL", nativeQuery = true)
    int updateCodeFromClientInfo();

}
