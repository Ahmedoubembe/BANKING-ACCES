package com.bamis.banking_access.repository;

import com.bamis.banking_access.entity.ClientInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientInfoRepository extends JpaRepository<ClientInfo, String> {
    Optional<ClientInfo> findByPhoneNumber(String phoneNumber);
}