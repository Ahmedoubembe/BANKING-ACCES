package com.bamis.banking_access.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom", unique = true, nullable = false)
    private String nom;

    @Column(name = "description")
    private String description;

    @Column(name = "est_actif")
    private Boolean estActif = true;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    // Relation Many-to-Many avec User - LAZY pour éviter les problèmes de performance
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<User> users = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }

    // Constructeur utilitaire
    public Role(String nom, String description) {
        this.nom = nom;
        this.description = description;
        this.estActif = true;
    }

    // Énumération des rôles standard
    public static class RoleNames {
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String GESTIONNAIRE = "ROLE_GESTIONNAIRE";
        public static final String CHEF_AGENCE = "ROLE_CHEF_AGENCE";
        public static final String DIRECTEUR = "ROLE_DIRECTEUR";
        public static final String AUDIT = "ROLE_AUDIT";
    }

    // Méthodes equals et hashCode basées sur l'ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return id != null && id.equals(role.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", estActif=" + estActif +
                '}';
    }
}