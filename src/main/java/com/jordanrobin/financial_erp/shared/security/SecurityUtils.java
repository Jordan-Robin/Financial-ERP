package com.jordanrobin.financial_erp.shared.security;

import com.jordanrobin.financial_erp.domain.auth.user.CustomUserDetails;
import com.jordanrobin.financial_erp.domain.auth.user.User;
import com.jordanrobin.financial_erp.shared.exception.SecurityExceptions;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@NullMarked
public class SecurityUtils {

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (
            auth == null
            || !auth.isAuthenticated()
            || !(auth.getPrincipal() instanceof CustomUserDetails userDetails)
        ) {
            throw new SecurityExceptions.UnauthorizedException("No authenticated user found.");
        }

        return ((CustomUserDetails) Objects.requireNonNull(auth.getPrincipal())).getUser();
    }

    public String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

}
