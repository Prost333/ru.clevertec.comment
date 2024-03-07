package ru.clevertec.comment.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.clevertec.comment.multiFeign.UserClient;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityConfigTest {
    @Autowired
    private SecurityConfig securityConfig;

    @Test
    void jwtSecretKey() {
        SecretKey secretKey = securityConfig.jwtSecretKey();
        assertNotNull(secretKey);
    }

    @Test
    void jwtService() {
        JwtService jwtService = securityConfig.jwtService();
        assertNotNull(jwtService);
    }

    @Test
    void userClient() {
        UserClient userClient = securityConfig.userClient();
        assertNotNull(userClient);
    }

    @Test
    void jwtTokenFilter() {
        JwtTokenFilter jwtTokenFilter = securityConfig.jwtTokenFilter();
        assertNotNull(jwtTokenFilter);
    }
}