package ru.clevertec.comment.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtServiceTest {
    String key="c92cef2481ba7a58dc63485105e6c0bc289670ba31108ff8cdf6402ae92dc84e";
    @Test
    public void testGenerateToken() {
        JwtService jwtService = new JwtService();
        UserDetails userDetails = mock(UserDetails.class);
        ReflectionTestUtils.setField(jwtService, "secret", key);
        ReflectionTestUtils.setField(jwtService, "expirationTime", 3600000);

        String token = jwtService.generateToken(userDetails);

        assertTrue(token.length() > 0);
    }

    @Test
    public void testExtractUserName() {
        JwtService jwtService = new JwtService();
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        ReflectionTestUtils.setField(jwtService, "secret", key);
        ReflectionTestUtils.setField(jwtService, "expirationTime", 3600000);

        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUserName(token);

        assertEquals("testUser", username);
    }

    @Test
    public void testIsTokenValid() {
        JwtService jwtService = new JwtService();
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        ReflectionTestUtils.setField(jwtService, "secret", key);
        ReflectionTestUtils.setField(jwtService, "expirationTime", 3600000);

        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }
}