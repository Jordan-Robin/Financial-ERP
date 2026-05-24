package com.jordanrobin.financial_erp.domain.auth.invitationtoken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface InvitationTokenRepository extends JpaRepository<InvitationToken, UUID> {

    @Query("SELECT r FROM InvitationToken r JOIN FETCH r.user u WHERE r.tokenHash = :tokenHash")
    Optional<InvitationToken> findByTokenHashWithUser(String tokenHash);
}
