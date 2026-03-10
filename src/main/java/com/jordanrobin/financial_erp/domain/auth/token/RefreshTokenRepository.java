package com.jordanrobin.financial_erp.domain.auth.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Query("SELECT r FROM RefreshToken r " +
        "JOIN FETCH r.user u " +
        "WHERE r.token = :token")
    Optional<RefreshToken> findByTokenWithUser(String token);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.id = :id")
    void revokeById(Long id);
}