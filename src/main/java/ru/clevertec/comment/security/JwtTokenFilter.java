package ru.clevertec.comment.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.clevertec.comment.multiFeign.UserClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JwtTokenFilter is responsible for intercepting incoming HTTP requests,
 * extracting JWT token from the request, validating it, and setting up the
 * authentication context if the token is valid.
 */
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final String key;
    private final UserClient userClient;

    /**
     * Constructs a JwtTokenFilter with the provided key and user client.
     *
     * @param key         The secret key used for JWT token verification.
     * @param userClient  The client used for retrieving user details.
     */
    public JwtTokenFilter(String key,  UserClient userClient) {
        this.key = key;
        this.userClient = userClient;

    }

    /**
     * Intercept the incoming HTTP request, extract JWT token from the request,
     * validate it, and set up the authentication context if the token is valid.
     *
     * @param request     The HTTP servlet request.
     * @param response    The HTTP servlet response.
     * @param filterChain The filter chain for continuing the filter execution.
     * @throws ServletException If an exception occurs during the filter execution.
     * @throws IOException      If an I/O exception occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String responseBody = userClient.getDto();
        String authorizationHeader = "Bearer " + responseBody;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            List<GrantedAuthority> authorities = Arrays.stream(claims.get("role").toString().split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            JwtAuthenticationToken authentication = new JwtAuthenticationToken(username, token, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
