package com.jordanrobin.financial_erp.domain.auth.token;

import com.jordanrobin.financial_erp.domain.auth.user.User;
import com.jordanrobin.financial_erp.infrastructure.security.JwtProperties;
import com.jordanrobin.financial_erp.shared.exception.domain.AuthExceptions.InvalidRefreshTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    @Transactional
    public RefreshToken create(User user) {
        return refreshTokenRepository.save(
            RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(Instant.now().plus(jwtProperties.refreshTokenExpirySeconds(), ChronoUnit.SECONDS))
                .build()
        );
    }

    @Transactional
    public RefreshToken validateAndRotate(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenWithUser(token)
            .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token introuvable"));

        if (refreshToken.isRevoked()) {
            throw new InvalidRefreshTokenException("Refresh token révoqué");
        }

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidRefreshTokenException("Refresh token expiré");
        }

        refreshToken.setRevoked(true);

        return refreshToken;
    }

    @Transactional
    public void revoke(String token) {
        refreshTokenRepository.findByToken(token)
            .ifPresent(t -> t.setRevoked(true));
    }
}
