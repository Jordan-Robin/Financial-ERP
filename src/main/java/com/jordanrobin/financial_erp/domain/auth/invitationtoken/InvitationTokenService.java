package com.jordanrobin.financial_erp.domain.auth.invitationtoken;

import com.jordanrobin.financial_erp.domain.auth.invitationtoken.dtos.CreateTokenResult;
import com.jordanrobin.financial_erp.domain.auth.user.User;
import com.jordanrobin.financial_erp.infrastructure.security.InvitationProperties;
import com.jordanrobin.financial_erp.shared.utils.HashUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvitationTokenService {

    private final InvitationTokenRepository tokenRepository;
    private final InvitationProperties invitationProperties;

    @Transactional
    public CreateTokenResult createToken(User user) {
        String rawToken = UUID.randomUUID().toString();

        InvitationToken invitationToken = tokenRepository.save(
            InvitationToken.builder()
                .user(user)
                .tokenHash(HashUtils.sha256(rawToken))
                .expiresAt(Instant.now().plus(invitationProperties.expirySeconds(), ChronoUnit.SECONDS))
                .build()
        );

        return new CreateTokenResult(invitationToken, rawToken);
    }

    public Optional<InvitationToken> getTokenWithUser(String tokenHash) {
        return tokenRepository.findByTokenHashWithUser(tokenHash);
    }

    @Transactional
    public void markAsUsed(InvitationToken token) {
        token.setUsedAt(Instant.now());
        tokenRepository.save(token);
    }
}
