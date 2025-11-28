package com.producttrial.back.service;

import com.producttrial.back.entity.User;
import com.producttrial.back.service.serviceimpl.AuthorizationServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {
    @Mock
    IUserService userService;

    @InjectMocks
    private AuthorizationServiceImpl authorizationService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void currentUserEmail_returnsName_whenAuthenticated() {
        User user = User.builder()
                .id(1L)
                .username("test")
                .firstname("Test")
                .email("admin@admin.com")
                .build();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("1", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userService.findById(1L)).thenReturn(Optional.of(user));

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
        User user = User.builder()
                .id(1L)
                .username("test")
                .firstname("Test")
                .email("admin@admin.com")
                .build();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("1", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userService.findById(1L)).thenReturn(Optional.of(user));


        assertDoesNotThrow(() -> authorizationService.ensureAdmin());
    }

    @Test
    void ensureAdmin_throws_forNonAdmin() {
        User user = User.builder()
                .id(1L)
                .username("test")
                .firstname("Test")
                .email("admin@admin.fr")
                .build();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("1", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userService.findById(1L)).thenReturn(Optional.of(user));

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
