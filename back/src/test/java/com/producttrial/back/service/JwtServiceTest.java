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
    private JwtService jwtService;

    private final String secret = "my-super-secret-key-32charslong!!!";
    private final long expiration = 3600000;

    @BeforeEach
    void setup() {
        jwtService = new JwtService();
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
    void generateToken_expirationIsSet() throws InterruptedException {
        String email = "user2@example.com";
        long before = System.currentTimeMillis();
        Thread.sleep(500); // pas ouf mais permet de s'assurer que les valeurs soient différent
        String token = jwtService.generateToken(email);
        long after = System.currentTimeMillis();

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertTrue(claims.getExpiration().getTime() >= before + expiration - 100,
                "Token expiration should be roughly now + expiration");
        assertTrue(claims.getExpiration().getTime() <= after + expiration + 100,
                "Token expiration should be roughly now + expiration");
    }
}
