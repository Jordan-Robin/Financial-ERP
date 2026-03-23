package com.jordanrobin.financial_erp.fixtures;

import com.jordanrobin.financial_erp.domain.auth.token.RefreshToken;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class JwtTokenFixtures {

    public static Jwt createTenantAdminToken() {
        return Jwt.withTokenValue("mock-token")
            .header("alg", "none")
            .subject("admin-id")
            .claim("scope", "TENANT_ADMIN")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(900))
            .build();
    }

    public static Jwt createViewerToken() {
        return Jwt.withTokenValue("mock-token")
            .header("alg", "none")
            .subject("viewer-id")
            .claim("scope", "VIEWER")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(900))
            .build();
    }

    public static RefreshToken.RefreshTokenBuilder createRefreshToken() {
        return RefreshToken.builder()
            .token("valid-token")
            .user(UserFixtures.adminUserBuilder().build())
            .revoked(false)
            .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS));
    }

}
