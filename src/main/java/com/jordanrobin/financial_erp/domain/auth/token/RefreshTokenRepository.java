package com.jordanrobin.financial_erp.domain.auth.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    @Query("SELECT r FROM RefreshToken r " +
        "JOIN FETCH r.user u " +
        "WHERE r.token = :token")
    Optional<RefreshToken> findByTokenWithUser(String token);
}