package com.producttrial.back.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceImplTest {
    private AuthorizationServiceImpl authorizationService;

    @BeforeEach
    void setUp() {
        authorizationService = new AuthorizationServiceImpl();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void currentUserEmail_returnsName_whenAuthenticated() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("admin@admin.com", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
        String email = authorizationService.currentUserEmail();

        assertEquals("admin@admin.com", email);
    }

    @Test
    void currentUserEmail_returnsNull_whenNoAuthentication() {
        SecurityContextHolder.clearContext();
        String email = authorizationService.currentUserEmail();

        assertNull(email);
    }

    @Test
    void currentUserEmail_returnsNull_whenNotAuthenticated() {
        UsernamePasswordAuthenticationToken unauthToken = new UsernamePasswordAuthenticationToken("user@test.com", "credentials");
        SecurityContextHolder.getContext().setAuthentication(unauthToken);
        String email = authorizationService.currentUserEmail();

        assertNull(email);
    }

    @Test
    void ensureAdmin_passes_forAdmin() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("admin@admin.com", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertDoesNotThrow(() -> authorizationService.ensureAdmin());
    }

    @Test
    void ensureAdmin_throws_forNonAdmin() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("user@test.com", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        AccessDeniedException ex = assertThrows(AccessDeniedException.class,
                () -> authorizationService.ensureAdmin());
        assertTrue(ex.getMessage().toLowerCase().contains("access denied"));
    }

    @Test
    void ensureAdmin_throws_whenNotAuthenticated() {
        UsernamePasswordAuthenticationToken unauthToken = new UsernamePasswordAuthenticationToken("user@test.com", "credentials");
        SecurityContextHolder.getContext().setAuthentication(unauthToken);

        assertThrows(AccessDeniedException.class, () -> authorizationService.ensureAdmin());
    }
}
