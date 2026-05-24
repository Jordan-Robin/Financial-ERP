package com.jordanrobin.financial_erp.domain.auth.jwt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String token);

    @Query("SELECT r FROM RefreshToken r " +
        "JOIN FETCH r.user u " +
        "WHERE r.tokenHash = :token")
    Optional<RefreshToken> findByTokenWithUser(String token);
}