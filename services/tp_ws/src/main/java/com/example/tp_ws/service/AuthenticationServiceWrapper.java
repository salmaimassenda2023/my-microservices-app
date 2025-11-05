package com.example.tp_ws.service;

import com.example.auth.client.AuthenticationPortType;
import com.example.auth.client.AuthenticationService;
import com.example.auth.client.UserProfile;
import jakarta.xml.ws.WebServiceException;
import org.springframework.stereotype.Service;
import java.net.MalformedURLException;
import java.net.URL;

@Service
public class AuthenticationServiceWrapper {

    private AuthenticationPortType port;

    public AuthenticationServiceWrapper() {
        try {
            URL wsdlURL = new URL("http://api.example.com/auth?wsdl");
            AuthenticationService service = new AuthenticationService(wsdlURL);
            this.port = service.getAuthenticationPort();
        } catch (MalformedURLException e) {
            throw new RuntimeException("URL WSDL invalide", e);
        }
    }

    /**
     * Authentifie un utilisateur
     */
    public boolean authenticate(String username, String password) {
        try {
            return port.authenticate(username, password);
        } catch (WebServiceException e) {
            System.err.println("Erreur lors de l'authentification: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Erreur inattendue: " + e.getMessage());
            return false;
        }
    }

    /**
     * Récupère le profil d'un utilisateur
     */
    public UserProfile getUserProfile(int userId) {
        try {
            return port.getUserProfile(userId);
        } catch (WebServiceException e) {
            System.err.println("Erreur lors de la récupération du profil: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Erreur inattendue: " + e.getMessage());
            return null;
        }
    }

    /**
     * Met à jour le profil d'un utilisateur
     */
    public boolean updateUserProfile(int userId, UserProfile profile) {
        try {
            return port.updateUserProfile(userId, profile);
        } catch (WebServiceException e) {
            System.err.println("Erreur lors de la mise à jour: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Erreur inattendue: " + e.getMessage());
            return false;
        }
    }
}
