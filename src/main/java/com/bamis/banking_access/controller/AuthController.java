package com.bamis.banking_access.controller;

import com.bamis.banking_access.entity.User;
import com.bamis.banking_access.entity.oracle.UserGlobal;
import com.bamis.banking_access.repository.UserRepository;
import com.bamis.banking_access.repository.oracle.UserGlobalRepository;
import com.bamis.banking_access.security.JwtUtil;
import com.bamis.banking_access.security.Sha256PasswordEncoder;
import com.bamis.banking_access.service.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserGlobalRepository userGlobalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> loginRequest) {

        String login = loginRequest.get("login");
        String password = loginRequest.get("password");

        System.out.println("Tentative de connexion pour le login: " + login);

        if (login == null || login.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Le login est obligatoire");
            return ResponseEntity.badRequest().body(error);
        }
        if (password == null || password.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Le mot de passe est obligatoire");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            // ÉTAPE 1 : Vérifier existence dans USERS (MySQL)
            User user = userRepository.findByNomUtilisateur(login).orElse(null);
            if (user == null) {
                System.out.println("Utilisateur non trouvé avec le login: " + login);
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Identifiants incorrects!");
                return ResponseEntity.badRequest().body(error);
            }

            System.out.println("Utilisateur trouvé: " + user.getNomUtilisateur() + " (ID: " + user.getId() + ")");

            if (!user.getEstActif()) {
                System.out.println("Compte inactif pour: " + login);
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Compte désactivé!");
                return ResponseEntity.badRequest().body(error);
            }

            // ÉTAPE 2 : Vérifier mot de passe dans USERGLOBAL (Oracle)
            Optional<UserGlobal> userGlobalOpt = userGlobalRepository.findByLogin(login);

            if (!userGlobalOpt.isPresent()) {
                System.out.println("Utilisateur non trouvé dans USERGLOBAL: " + login);
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Identifiants incorrects!");
                return ResponseEntity.badRequest().body(error);
            }

            UserGlobal userGlobal = userGlobalOpt.get();
            System.out.println("Utilisateur trouvé dans USERGLOBAL: " + userGlobal.getLogin());

            // ÉTAPE 3 : Vérification SHA-256 manuelle
            Sha256PasswordEncoder encoder = new Sha256PasswordEncoder();
            if (!encoder.matches(password, userGlobal.getPwd())) {
                System.out.println("Mot de passe incorrect");
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Identifiants incorrects!");
                return ResponseEntity.badRequest().body(error);
            }

            System.out.println("Mot de passe validé avec succès!");

            // ÉTAPE 4 : Charger UserPrincipal et générer JWT
            UserPrincipal userPrincipal = UserPrincipal.create(user, userGlobal.getPwd());
            String jwt = jwtUtils.generateToken(userPrincipal.getEmail());

            List<String> roles = userPrincipal.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            // Mettre à jour la dernière connexion
            try {
                user.setDerniereConnexion(LocalDateTime.now());
                userRepository.save(user);
            } catch (Exception e) {
                System.out.println("Erreur mise à jour dernière connexion: " + e.getMessage());
            }

            System.out.println("Connexion réussie pour: " + login);

            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("type", "Bearer");
            response.put("id", userPrincipal.getId());
            response.put("nomUtilisateur", userPrincipal.getNomUtilisateur());
            response.put("email", userPrincipal.getEmail());
            response.put("prenom", userPrincipal.getPrenom());
            response.put("nom", userPrincipal.getNom());
            response.put("roles", roles);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Erreur lors de l'authentification: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de l'authentification");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        if (authentication == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Utilisateur non authentifié");
            return ResponseEntity.badRequest().body(error);
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", userPrincipal.getId());
        userInfo.put("nomUtilisateur", userPrincipal.getNomUtilisateur());
        userInfo.put("email", userPrincipal.getEmail());
        userInfo.put("prenom", userPrincipal.getPrenom());
        userInfo.put("nom", userPrincipal.getNom());
        userInfo.put("telephone", userPrincipal.getTelephone());
        userInfo.put("roles", userPrincipal.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toList()));
        userInfo.put("derniereConnexion", userPrincipal.getDerniereConnexion());

        return ResponseEntity.ok(userInfo);
    }
}