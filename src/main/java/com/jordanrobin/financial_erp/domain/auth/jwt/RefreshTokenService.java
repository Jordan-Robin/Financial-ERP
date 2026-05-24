//package com.jordanrobin.financial_erp.domain.auth.jwt;
//
//import com.jordanrobin.financial_erp.domain.auth.user.User;
//import com.jordanrobin.financial_erp.infrastructure.security.JwtProperties;
//import com.jordanrobin.financial_erp.shared.exception.AuthExceptions.InvalidRefreshTokenException;
//import com.jordanrobin.financial_erp.shared.utils.HashUtils;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class RefreshTokenService {
//
//    private final RefreshTokenRepository refreshTokenRepository;
//    private final JwtProperties jwtProperties;
//
//    @Transactional
//    public RefreshToken create(User user) {
//        return refreshTokenRepository.save(
//            RefreshToken.builder()
//                .tokenHash(HashUtils.sha256(UUID.randomUUID().toString()))
//                .user(user)
//                .expiresAt(Instant.now().plus(jwtProperties.refreshTokenExpirySeconds(), ChronoUnit.SECONDS))
//                .build()
//        );
//    }
//
//    @Transactional
//    public RefreshToken validateAndRotate(String token) {
//        RefreshToken refreshToken = refreshTokenRepository.findByTokenWithUser(token)
//            .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token introuvable"));
//
//        if (refreshToken.getRevokedAt() != null) {
//            throw new InvalidRefreshTokenException("Refresh token révoqué");
//        }
//
//        if (refreshToken.getExpiresAt() != null && refreshToken.getExpiresAt().isBefore(Instant.now())) {
//            throw new InvalidRefreshTokenException("Refresh token expiré");
//        }
//
//        refreshToken.setRevoked(true);
//
//        return refreshToken;
//    }
//
//    @Transactional
//    public void revoke(String token) {
//        refreshTokenRepository.findByToken(token)
//            .ifPresent(t -> t.setRevoked(true));
//    }
//}
