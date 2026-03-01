
package com.bamis.banking_access.repository;

import com.bamis.banking_access.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByNom(String nom);
    boolean existsByNom(String nom);

    @Query("SELECT r FROM Role r WHERE r.estActif = true")
    List<Role> findAllActive();

    @Query("SELECT r FROM Role r JOIN r.users u WHERE u.id = :userId")
    List<Role> findByUserId(Long userId);
}
