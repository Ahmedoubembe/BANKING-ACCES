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
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_utilisateur", unique = true, nullable = false)
    private String nomUtilisateur;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    @Column(name = "prenom")
    private String prenom;

    @Column(name = "nom")
    private String nom;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "est_actif")
    private Boolean estActif = true;

    @Column(name = "compte_non_expire")
    private Boolean compteNonExpire = true;

    @Column(name = "compte_non_verrouille")
    private Boolean compteNonVerrouille = true;

    @Column(name = "identifiants_non_expires")
    private Boolean identifiantsNonExpires = true;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "derniere_connexion")
    private LocalDateTime derniereConnexion;

    @Column(name = "mot_de_passe_modifie_le")
    private LocalDateTime motDePasseModifieLe;

    // Ajoutez simplement ces 2 champs à votre classe User.java existante

    @Column(name = "agence")
    private String agence;

    @Column(name = "premier_login")
    private Boolean premierLogin = true;

    // Ajoutez ces getters/setters
    public String getAgence() {
        return agence;
    }

    public void setAgence(String agence) {
        this.agence = agence;
    }

    public Boolean getPremierLogin() {
        return premierLogin;
    }

    public void setPremierLogin(Boolean premierLogin) {
        this.premierLogin = premierLogin;
    }

    // Relation Many-to-Many avec Role
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "utilisateur_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
        motDePasseModifieLe = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }

    // Constructeur pour compatibilité avec l'ancien code
    public User(Long id, String nomUtilisateur, String motDePasse) {
        this.id = id;
        this.nomUtilisateur = nomUtilisateur;
        this.motDePasse = motDePasse;
        this.estActif = true;
        this.compteNonExpire = true;
        this.compteNonVerrouille = true;
        this.identifiantsNonExpires = true;
    }

    // Méthodes utilitaires pour les rôles
    public void ajouterRole(Role role) {
        this.roles.add(role);
        // Ne pas accéder à la collection users du rôle pour éviter LazyInitializationException
    }

    public void supprimerRole(Role role) {
        this.roles.remove(role);
        // Ne pas accéder à la collection users du rôle pour éviter LazyInitializationException
    }

    public boolean aRole(String nomRole) {
        return roles.stream()
                .anyMatch(role -> role.getNom().equals(nomRole));
    }
}