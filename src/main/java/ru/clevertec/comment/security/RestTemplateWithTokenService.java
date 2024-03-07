package ru.clevertec.comment.security;

import org.springframework.stereotype.Service;
/**
 * RestTemplateWithTokenService class provides a service to store and retrieve JWT token for making authenticated REST API calls.
 */
@Service
public class RestTemplateWithTokenService {
    private String jwtToken;
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    /**
     * Sets the JWT token for making authenticated REST API calls.
     *
     * @param jwtToken The JWT token to set.
     */
    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
    /**
     * Retrieves the stored JWT token.
     *
     * @return The stored JWT token.
     */
    public String getJwtToken() {
        return jwtToken;
    }


}
