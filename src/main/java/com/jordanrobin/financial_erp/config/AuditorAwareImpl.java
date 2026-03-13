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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        return Optional.ofNullable(authentication.getPrincipal())
            .filter(p -> p instanceof CustomUserDetails)
            .map(p -> ((CustomUserDetails) p).getUser().getId());
    }
}
