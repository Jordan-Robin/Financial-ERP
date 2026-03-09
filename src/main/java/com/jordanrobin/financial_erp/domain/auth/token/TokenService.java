package com.jordanrobin.financial_erp.domain.auth.token;

import com.jordanrobin.financial_erp.domain.auth.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {

    private static final long ACCESS_TOKEN_EXPIRY_SECONDS = 900L; // 15 min

    private final JwtEncoder jwtEncoder;

    public String generateAccessToken(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new IllegalStateException("Le principal d'authentification est invalide ou absent");
        }

        Instant now = Instant.now();

        String scope = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("https://financial-erp.com")
            .issuedAt(now)
            .expiresAt(now.plus(ACCESS_TOKEN_EXPIRY_SECONDS, ChronoUnit.SECONDS))
            .subject(userDetails.getUser().getId().toString())
            .claim("email", userDetails.getUsername())
            .claim("scope", scope)
            // .claim("tenant_id", null) TODO à alimenter quand multitenant sera implémenté
            .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public static long getAccessTokenExpirySeconds() {
        return ACCESS_TOKEN_EXPIRY_SECONDS;
    }
}
