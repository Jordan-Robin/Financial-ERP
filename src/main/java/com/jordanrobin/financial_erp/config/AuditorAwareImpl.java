package com.jordanrobin.financial_erp.config;

import com.jordanrobin.financial_erp.domain.auth.user.CustomUserDetails;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@NullMarked
public class AuditorAwareImpl implements AuditorAware<UUID> {

    @Override
    public Optional<UUID> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .filter(Authentication::isAuthenticated)
            .map(auth -> {
                if (auth.getPrincipal() instanceof CustomUserDetails userDetails) {
                    return userDetails.getUser().getId();
                }
                return null;
            });
    }
}
