//package com.example.jwt_demo.config;
//
//import com.example.jwt_demo.model.Role;
//import com.example.jwt_demo.model.User;
//import com.example.jwt_demo.repository.RoleRepository;
//import com.example.jwt_demo.repository.UserRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.HashSet;
//import java.util.Set;
//
//@Component
//public class DataInitializer implements CommandLineRunner {
//
//    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Override
//    @Transactional // Important : Transaction pour éviter les LazyInitializationException
//    public void run(String... args) throws Exception {
//        initializeRoles();
//        initializeAdminUser();
//    }
//
//    @Transactional
//    public void initializeRoles() {
//        logger.info("Initialisation des rôles...");
//
//        createRoleIfNotExists(Role.RoleNames.ADMIN, "Administration complète du système");
//        createRoleIfNotExists(Role.RoleNames.GESTIONNAIRE, "Traitement et validation directe des demandes");
//        createRoleIfNotExists(Role.RoleNames.CHEF_AGENCE, "Validation intermédiaire et gestion d'agence");
//        createRoleIfNotExists(Role.RoleNames.DIRECTEUR, "Validation finale des demandes stratégiques");
//        createRoleIfNotExists(Role.RoleNames.AUDIT, "Accès en lecture seule pour audit et contrôle");
//
//        logger.info("Initialisation des rôles terminée.");
//    }
//
//    private void createRoleIfNotExists(String nom, String description) {
//        if (!roleRepository.existsByNom(nom)) {
//            Role role = new Role();
//            role.setNom(nom);
//            role.setDescription(description);
//            role.setEstActif(true);
//            roleRepository.save(role);
//            logger.info("Rôle créé: {}", nom);
//        } else {
//            logger.debug("Rôle déjà existant: {}", nom);
//        }
//    }
//
//    @Transactional
//    public void initializeAdminUser() {
//        logger.info("Initialisation de l'utilisateur administrateur...");
//
//        String adminEmail = "bamis@bamis.bamis";
//
//        if (!userRepository.existsByEmail(adminEmail)) {
//            // Récupération du rôle ADMIN
//            Role adminRole = roleRepository.findByNom(Role.RoleNames.ADMIN)
//                    .orElseThrow(() -> new RuntimeException("Rôle ADMIN non trouvé"));
//
//            // Création de l'utilisateur admin
//            User admin = new User();
//            admin.setNomUtilisateur("admin");
//            admin.setEmail(adminEmail);
//            admin.setMotDePasse(passwordEncoder.encode("bamis@123"));
//            admin.setPrenom("Administrateur");
//            admin.setNom("Système");
//            admin.setTelephone("0000000000");
//            admin.setEstActif(true);
//            admin.setCompteNonExpire(true);
//            admin.setCompteNonVerrouille(true);
//            admin.setIdentifiantsNonExpires(true);
//
//            // Attribution du rôle ADMIN de manière sécurisée
//            Set<Role> roles = new HashSet<>();
//            roles.add(adminRole);
//            admin.setRoles(roles);
//
//            userRepository.save(admin);
//            logger.info("Utilisateur administrateur créé avec l'email: {}", adminEmail);
//        } else {
//            logger.debug("Utilisateur administrateur déjà existant: {}", adminEmail);
//        }
//
//        logger.info("Initialisation de l'utilisateur administrateur terminée.");
//    }
//}








//
//
//package com.bamis.banking_access.config;
//
//import com.bamis.banking_access.entity.Role;
//import com.bamis.banking_access.entity.User;
//import com.bamis.banking_access.repository.RoleRepository;
//import com.bamis.banking_access.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.HashSet;
//import java.util.Set;
//
//@Component
//public class DataInitializer implements CommandLineRunner {
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    @Transactional
//    public void run(String... args) throws Exception {
//        initializeRoles();
//        initializeAdminUser();
//    }
//
//    @Transactional
//    public void initializeRoles() {
//        System.out.println("Initialisation des rôles...");
//
//        createRoleIfNotExists(Role.RoleNames.ADMIN, "Administration complète du système");
//        createRoleIfNotExists(Role.RoleNames.GESTIONNAIRE, "Traitement et validation directe des demandes");
//        createRoleIfNotExists(Role.RoleNames.CHEF_AGENCE, "Validation intermédiaire et gestion d'agence");
//        createRoleIfNotExists(Role.RoleNames.DIRECTEUR, "Validation finale des demandes stratégiques");
//        createRoleIfNotExists(Role.RoleNames.AUDIT, "Accès en lecture seule pour audit et contrôle");
//
//        System.out.println("Initialisation des rôles terminée.");
//    }
//
//    private void createRoleIfNotExists(String nom, String description) {
//        if (!roleRepository.existsByNom(nom)) {
//            Role role = new Role();
//            role.setNom(nom);
//            role.setDescription(description);
//            role.setEstActif(true);
//            roleRepository.save(role);
//            System.out.println("Rôle créé: " + nom);
//        } else {
//            System.out.println("Rôle déjà existant: " + nom);
//        }
//    }
//
//    @Transactional
//    public void initializeAdminUser() {
//        System.out.println("Initialisation de l'utilisateur administrateur...");
//
//        String adminEmail = "bamis@bamis.bamis";
//
//        if (!userRepository.existsByEmail(adminEmail)) {
//            // Récupération du rôle ADMIN
//            Role adminRole = roleRepository.findByNom(Role.RoleNames.ADMIN)
//                    .orElseThrow(() -> new RuntimeException("Rôle ADMIN non trouvé"));
//
//            // Création de l'utilisateur admin SANS mot de passe
//            // Le mot de passe doit exister dans USERGLOBAL (Oracle)
//            User admin = new User();
////            admin.setNomUtilisateur("admin");
//            admin.setEmail(adminEmail);
//            // PAS DE MOT DE PASSE ICI - Il est dans USERGLOBAL
//            admin.setPrenom("Administrateur");
//            admin.setNom("Système");
//            admin.setTelephone("0000000000");
//            admin.setEstActif(true);
//            admin.setCompteNonExpire(true);
//            admin.setCompteNonVerrouille(true);
//            admin.setIdentifiantsNonExpires(true);
//            admin.setPremierLogin(false);
//
//            // Attribution du rôle ADMIN
//            Set<Role> roles = new HashSet<>();
//            roles.add(adminRole);
//            admin.setRoles(roles);
//
//            userRepository.save(admin);
//            System.out.println("Utilisateur administrateur créé avec l'email: " + adminEmail);
//            System.out.println("IMPORTANT: Le mot de passe doit exister dans USERGLOBAL (Oracle) avec le login: " + adminEmail);
//        } else {
//            System.out.println("Utilisateur administrateur déjà existant: " + adminEmail);
//        }
//
//        System.out.println("Initialisation de l'utilisateur administrateur terminée.");
//    }
//}