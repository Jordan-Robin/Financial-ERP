package com.jordanrobin.financial_erp.domain.auth.token;
import com.jordanrobin.financial_erp.domain.auth.user.User;
import com.jordanrobin.financial_erp.shared.exception.domain.AuthExceptions.InvalidRefreshTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User user;
    private RefreshToken validToken;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .id(1L)
            .email("test@test.com")
            .build();

        validToken = RefreshToken.builder()
            .id(1L)
            .token("valid-token")
            .user(user)
            .revoked(false)
            .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
            .build();
    }

    @Test
    void create_shouldSaveRefreshTokenWithCorrectExpiration() {
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

        RefreshToken result = refreshTokenService.create(user);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());

        RefreshToken saved = captor.getValue();
        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.isRevoked()).isFalse();
        assertThat(saved.getExpiresAt()).isAfter(Instant.now().plus(6, ChronoUnit.DAYS));
        assertThat(saved.getToken()).isNotBlank();
    }

    @Test
    void create_shouldGenerateUniqueTokens() {
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

        RefreshToken token1 = refreshTokenService.create(user);
        RefreshToken token2 = refreshTokenService.create(user);

        assertThat(token1.getToken()).isNotEqualTo(token2.getToken());
    }

    @Test
    void validateAndRotate_shouldRevokeTokenAndReturnIt() {
        when(refreshTokenRepository.findByTokenWithUser("valid-token")).thenReturn(Optional.of(validToken));

        RefreshToken result = refreshTokenService.validateAndRotate("valid-token");

        verify(refreshTokenRepository).revokeById(validToken.getId());
        assertThat(result).isEqualTo(validToken);
    }

    @Test
    void validateAndRotate_shouldThrowWhenTokenNotFound() {
        when(refreshTokenRepository.findByTokenWithUser("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.validateAndRotate("unknown"))
            .isInstanceOf(InvalidRefreshTokenException.class)
            .hasMessageContaining("introuvable");
    }

    @Test
    void validateAndRotate_shouldThrowWhenTokenRevoked() {
        validToken.setRevoked(true);
        when(refreshTokenRepository.findByTokenWithUser("valid-token")).thenReturn(Optional.of(validToken));

        assertThatThrownBy(() -> refreshTokenService.validateAndRotate("valid-token"))
            .isInstanceOf(InvalidRefreshTokenException.class)
            .hasMessageContaining("révoqué");
    }

    @Test
    void validateAndRotate_shouldThrowWhenTokenExpired() {
        validToken.setExpiresAt(Instant.now().minus(1, ChronoUnit.DAYS));
        when(refreshTokenRepository.findByTokenWithUser("valid-token")).thenReturn(Optional.of(validToken));

        assertThatThrownBy(() -> refreshTokenService.validateAndRotate("valid-token"))
            .isInstanceOf(InvalidRefreshTokenException.class)
            .hasMessageContaining("expiré");
    }

    @Test
    void revoke_shouldRevokeToken() {
        when(refreshTokenRepository.findByToken("valid-token")).thenReturn(Optional.of(validToken));

        refreshTokenService.revoke("valid-token");

        verify(refreshTokenRepository).revokeById(validToken.getId());
    }

    @Test
    void revoke_shouldThrowWhenTokenNotFound() {
        when(refreshTokenRepository.findByToken("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.revoke("unknown"))
            .isInstanceOf(InvalidRefreshTokenException.class)
            .hasMessageContaining("introuvable");
    }
}
