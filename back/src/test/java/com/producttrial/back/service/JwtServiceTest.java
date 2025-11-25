package com.producttrial.back.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    private JwtServiceImpl jwtService;

    private final String secret = "my-super-secret-key-32charslong!!!";
    private final long expiration = 3600000;

    @BeforeEach
    void setup() {
        jwtService = new JwtServiceImpl();
        ReflectionTestUtils.setField(jwtService, "secret", secret);
        ReflectionTestUtils.setField(jwtService, "expiration", expiration);
    }

    @Test
    void generateToken_returnsToken() {
        String email = "test@test.fr";
        String token = jwtService.generateToken(email);

        assertNotNull(token, "Token should not be null");
        assertFalse(token.isEmpty(), "Token should not be empty");
    }

    @Test
    void generateToken_containsEmail() {
        String email = "test@test.fr";
        String token = jwtService.generateToken(email);

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(email, claims.getSubject(), "Token subject should be the email");
    }

    @Test
    void extractEmail_returnsEmail() {
        String email = "admin@test.com";
        String token = jwtService.generateToken(email);

        String extracted = jwtService.extractEmail(token);

        assertEquals(email, extracted, "Email should be extracted from token");
    }

    @Test
    void extractEmail_throwsExceptionOnEmptyToken() {
        assertThrows(IllegalArgumentException.class, () -> jwtService.extractEmail(""));
    }

    @Test
    void isTokenValid_returnsTrueOnValidToken() {
        String token = jwtService.generateToken("user@test.com");
        boolean result = jwtService.isValidToken(token);

        assertTrue(result, "Token should be valid");
    }

    @Test
    void isTokenValid_returnsFalseOnInvalidToken() {
        boolean result = jwtService.isValidToken("invalid");
        assertFalse(result, "Invalid token should not be valid");
    }

    @Test
    void isTokenValid_returnsFalseOnEmptyToken() {
        boolean result = jwtService.isValidToken("");
        assertFalse(result, "Empty token should not be valid");
    }
}
