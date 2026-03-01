package com.bamis.banking_access.service;

import com.bamis.banking_access.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {
    private Long id;
    private String nomUtilisateur;
    private String email;
    private String motDePasse;
    private String prenom;
    private String nom;
    private String telephone;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean estActif;
    private boolean compteNonExpire;
    private boolean compteNonVerrouille;
    private boolean identifiantsNonExpires;
    private LocalDateTime derniereConnexion;

    public UserPrincipal(Long id, String nomUtilisateur, String email, String motDePasse,
                         String prenom, String nom, String telephone,
                         Collection<? extends GrantedAuthority> authorities,
                         boolean estActif, boolean compteNonExpire,
                         boolean compteNonVerrouille, boolean identifiantsNonExpires,
                         LocalDateTime derniereConnexion) {
        this.id = id;
        this.nomUtilisateur = nomUtilisateur;
        this.email = email;
        this.motDePasse = motDePasse;
        this.prenom = prenom;
        this.nom = nom;
        this.telephone = telephone;
        this.authorities = authorities;
        this.estActif = estActif;
        this.compteNonExpire = compteNonExpire;
        this.compteNonVerrouille = compteNonVerrouille;
        this.identifiantsNonExpires = identifiantsNonExpires;
        this.derniereConnexion = derniereConnexion;
    }

    /**
     * Créer un UserPrincipal depuis User (MySQL) et mot de passe de UserGlobal (Oracle)
     *
     * @param user L'utilisateur de la base MySQL
     * @param passwordFromOracle Le mot de passe hashé SHA-256 depuis Oracle
     * @return UserPrincipal
     */
    public static UserPrincipal create(User user, String passwordFromOracle) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getNom()))
                .collect(Collectors.toList());

        return new UserPrincipal(
                user.getId(),
                user.getNomUtilisateur(),
                user.getEmail(),
                passwordFromOracle,
                user.getPrenom(),
                user.getNom(),
                user.getTelephone(),
                authorities,
                user.getEstActif(),
                user.getCompteNonExpire(),
                user.getCompteNonVerrouille(),
                user.getIdentifiantsNonExpires(),
                user.getDerniereConnexion()
        );
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getNom() {
        return nom;
    }

    public String getTelephone() {
        return telephone;
    }

    public LocalDateTime getDerniereConnexion() {
        return derniereConnexion;
    }

    public String getNomComplet() {
        return (prenom != null ? prenom : "") + " " + (nom != null ? nom : "").trim();
    }

    public boolean hasRole(String roleName) {
        return authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals(roleName));
    }

    public boolean hasAnyRole(String... roleNames) {
        for (String roleName : roleNames) {
            if (hasRole(roleName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return motDePasse;
    }

    @Override
    public String getUsername() {
        return nomUtilisateur;
    }

    @Override
    public boolean isAccountNonExpired() {
        return compteNonExpire;
    }

    @Override
    public boolean isAccountNonLocked() {
        return compteNonVerrouille;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return identifiantsNonExpires;
    }

    @Override
    public boolean isEnabled() {
        return estActif;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal that = (UserPrincipal) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}