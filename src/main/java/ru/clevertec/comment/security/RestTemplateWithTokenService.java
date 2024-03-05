package ru.clevertec.comment.security;

import org.springframework.stereotype.Service;

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

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getJwtToken() {
        return jwtToken;
    }


}
