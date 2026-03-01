package com.bamis.banking_access.repository.oracle;

import com.bamis.banking_access.entity.oracle.UserGlobal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserGlobalRepository extends JpaRepository<UserGlobal, String> {

    Optional<UserGlobal> findByLogin(String login);

    Optional<UserGlobal> findByEmail(String email);

//    @Query("SELECT ug FROM UserGlobal ug WHERE LOWER(ug.email) = :email")
//    Optional<UserGlobal> findByEmailIgnoreCase(@Param("email") String email);

    @Query("SELECT ug FROM UserGlobal ug WHERE LOWER(ug.login) = :login")
    Optional<UserGlobal> findByLoginIgnoreCase(@Param("login") String login);

    boolean existsByLogin(String login);
//
    boolean existsByEmail(String email);
}