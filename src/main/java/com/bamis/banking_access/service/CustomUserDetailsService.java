package com.bamis.banking_access.service;

import com.bamis.banking_access.entity.oracle.UserGlobal;
import com.bamis.banking_access.repository.UserRepository;
import com.bamis.banking_access.repository.oracle.UserGlobalRepository;
import com.bamis.banking_access.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserGlobalRepository userGlobalRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("Chargement de l'utilisateur: " + username);

        // ÉTAPE 1 : Charger l'utilisateur depuis USERS (MySQL)
        Optional<User> userOpt = userRepository.findByNomUtilisateur(username);

        if (!userOpt.isPresent()) {
            System.out.println("Utilisateur non trouvé dans USERS (MySQL): " + username);
            throw new UsernameNotFoundException("Utilisateur non trouvé: " + username);
        }

        User user = userOpt.get();
        System.out.println("Utilisateur trouvé dans USERS: " + user.getNomUtilisateur() + " (ID: " + user.getId() + ")");

        // Vérifier le statut du compte dans USERS
        if (!user.getEstActif()) {
            System.out.println("Compte inactif: " + username);
            throw new UsernameNotFoundException("Compte désactivé: " + username);
        }

        // ÉTAPE 2 : Charger le mot de passe depuis USERGLOBAL (Oracle)
        Optional<UserGlobal> userGlobalOpt = userGlobalRepository.findByLogin(username);

        if (!userGlobalOpt.isPresent()) {
            // Essayer avec l'email comme fallback
            userGlobalOpt = userGlobalRepository.findByEmail(username);
        }

        if (!userGlobalOpt.isPresent()) {
            System.out.println("INCOHÉRENCE: Utilisateur existe dans USERS mais pas dans USERGLOBAL: " + username);
            throw new UsernameNotFoundException("Utilisateur non trouvé dans la base globale: " + username);
        }

        UserGlobal userGlobal = userGlobalOpt.get();
        System.out.println("Mot de passe récupéré depuis USERGLOBAL pour: " + userGlobal.getLogin());

        // ÉTAPE 3 : Créer le UserPrincipal avec le mot de passe de USERGLOBAL
        UserPrincipal userPrincipal = UserPrincipal.create(user, userGlobal.getPwd());

        System.out.println("UserPrincipal créé avec succès pour: " + username);
        return userPrincipal;
    }
}