package com.producttrial.back.service.serviceimpl;

import com.producttrial.back.entity.User;
import com.producttrial.back.service.IAuthorizationService;
import com.producttrial.back.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationServiceImpl implements IAuthorizationService {
    private final IUserService userService;
    private static final String ADMIN_EMAIL = "admin@admin.com";

    @Override
    public String currentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        User user = userService.findById(Long.parseLong(auth.getName())).orElseThrow(() -> new IllegalStateException("Current user not found"));

        return user.getEmail();
    }

    @Override
    public void ensureAdmin() {
        String email = currentUserEmail();
        if (email == null || !email.equalsIgnoreCase(ADMIN_EMAIL)) {
            throw new AccessDeniedException("Access denied: only admin can access this resource");
        }
    }

    @Override
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        return Long.parseLong(auth.getName());
    }
}
