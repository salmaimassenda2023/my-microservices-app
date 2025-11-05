package com.example.tp_ws;

import com.example.auth.client.UserProfile;
import com.example.tp_ws.service.AuthenticationServiceWrapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestAuthenticationClient {

    public static void main(String[] args) {
        SpringApplication.run(TestAuthenticationClient.class, args);
    }

    @Bean
    public CommandLineRunner run(AuthenticationServiceWrapper authService) {
        return args -> {
            System.out.println("=== Test du Service d'Authentification ===\n");

            // 1. Authentification
            String username = "testuser";
            String password = "password123";

            System.out.println("1. Tentative d'authentification...");
            boolean isAuthenticated = authService.authenticate(username, password);

            if (isAuthenticated) {
                System.out.println("✓ Authentification réussie pour: " + username);

                // 2. Récupération du profil
                int userId = 1;
                System.out.println("\n2. Récupération du profil utilisateur...");
                UserProfile profile = authService.getUserProfile(userId);

                if (profile != null) {
                    System.out.println("✓ Profil récupéré:");
                    displayProfile(profile);

                    // 3. Mise à jour optionnelle
                    System.out.println("\n3. Test de mise à jour (optionnel)");
                    // Décommenter pour tester la mise à jour
                    // profile.setEmail("nouveau@email.com");
                    // boolean updated = authService.updateUserProfile(userId, profile);
                    // System.out.println("Mise à jour: " + (updated ? "✓ Réussie" : "✗ Échouée"));
                } else {
                    System.out.println("✗ Impossible de récupérer le profil");
                }
            } else {
                System.out.println("✗ Échec de l'authentification");
            }

            System.out.println("\n=== Fin du test ===");
        };
    }

    private void displayProfile(UserProfile profile) {
        System.out.println("  - ID: " + profile.getUserId());
        System.out.println("  - Nom d'utilisateur: " + profile.getUsername());
        System.out.println("  - Email: " + profile.getEmail());
        System.out.println("  - Nom complet: " + profile.getFullName());
    }
}