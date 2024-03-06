package ru.clevertec.comment.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private final String jwtToken;

    public JwtAuthenticationToken(String username, String jwtToken, Collection<? extends GrantedAuthority> authorities) {
        super(username, null, authorities);
        this.jwtToken = jwtToken;
    }

    public String getJwtToken() {
        return jwtToken;
    }

}
