// src/main/java/com/example/jwt_demo/repository/UserRepository.java
package com.bamis.banking_access.repository;

import com.bamis.banking_access.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Méthodes existantes - gardez celles que vous avez déjà
    Optional<User> findByNomUtilisateur(String login);

    Optional<User> findByEmail(String email);

    // Correction de la méthode problématique
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.estActif = true")
    Optional<User> findByEmailAndEstActif(@Param("email") String email);

    boolean existsByNomUtilisateur(String nomUtilisateur);

    boolean existsByEmail(String email);

    // NOUVELLES méthodes simples pour le CRUD
    List<User> findByEstActif(Boolean estActif);

    @Query("SELECT u FROM User u WHERE u.agence = :agence")
    List<User> findByAgence(@Param("agence") String agence);

    @Query("SELECT u FROM User u WHERE u.estActif = true")
    List<User> findAllActive();
}