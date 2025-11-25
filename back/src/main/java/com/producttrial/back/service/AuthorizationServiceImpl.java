package com.producttrial.back.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationServiceImpl implements IAuthorizationService {

    private static final String ADMIN_EMAIL = "admin@admin.com";

    public String currentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        return auth.getName();
    }

    public void ensureAdmin() {
        String email = currentUserEmail();
        if (email == null || !email.equalsIgnoreCase(ADMIN_EMAIL)) {
            throw new AccessDeniedException("Access denied: only admin can access this resource");
        }
    }
}
