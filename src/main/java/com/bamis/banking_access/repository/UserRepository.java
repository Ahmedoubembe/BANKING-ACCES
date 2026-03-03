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
    @Query(value = "SELECT * FROM banking_users where nom_utilisateur = :login" , nativeQuery = true)
    Optional<User> findByNomUtilisateur(@Param("login") String login);
//

    Optional<User> findByEmail(String email);

    // Correction de la méthode problématique
//    @Query("SELECT u FROM User u WHERE u.email = :email AND u.estActif = true"

    @Query(value = "SELECT * FROM banking_users WHERE email = :email AND est_actif = true", nativeQuery = true)
    Optional<User> findByEmailAndEstActif(@Param("email") String email);

    @Query(value = "SELECT * FROM banking_users WHERE est_actif = :estActif", nativeQuery = true)
    List<User> findByEstActif(@Param("estActif") Boolean estActif);

    @Query(value = "SELECT * FROM banking_users WHERE agence = :agence", nativeQuery = true)
    List<User> findByAgence(@Param("agence") String agence);

    @Query(value = "SELECT * FROM banking_users WHERE est_actif = true", nativeQuery = true)
    List<User> findAllActive();
}