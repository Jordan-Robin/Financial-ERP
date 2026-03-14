package com.jordanrobin.financial_erp.domain.auth.token;

import com.jordanrobin.financial_erp.domain.auth.user.User;
import com.jordanrobin.financial_erp.shared.exception.domain.AuthExceptions.InvalidRefreshTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenService")
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
            .email("test@test.com")
            .build();

        validToken = RefreshToken.builder()
            .token("valid-token")
            .user(user)
            .revoked(false)
            .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
            .build();
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("Sauvegarde un token avec les bonnes propriétés")
        void shouldSaveRefreshTokenWithCorrectProperties() {
            when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(i -> i.getArgument(0));

            refreshTokenService.create(user);

            ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
            verify(refreshTokenRepository).save(captor.capture());

            RefreshToken saved = captor.getValue();
            assertThat(saved.getUser()).isEqualTo(user);
            assertThat(saved.isRevoked()).isFalse();
            assertThat(saved.getExpiresAt()).isAfter(Instant.now().plus(6, ChronoUnit.DAYS));
            assertThat(saved.getToken()).isNotBlank();
        }

        @Test
        @DisplayName("Génère un token unique à chaque appel")
        void shouldGenerateUniqueTokens() {
            when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(i -> i.getArgument(0));

            RefreshToken token1 = refreshTokenService.create(user);
            RefreshToken token2 = refreshTokenService.create(user);

            assertThat(token1.getToken()).isNotEqualTo(token2.getToken());
        }
    }

    @Nested
    @DisplayName("validateAndRotate()")
    class ValidateAndRotate {

        @Test
        @DisplayName("Révoque le token et le retourne")
        void shouldRevokeTokenAndReturnIt() {
            when(refreshTokenRepository.findByTokenWithUser("valid-token"))
                .thenReturn(Optional.of(validToken));

            RefreshToken result = refreshTokenService.validateAndRotate("valid-token");

            assertThat(validToken.isRevoked()).isTrue();
            assertThat(result).isEqualTo(validToken);
        }

        @Test
        @DisplayName("Lève InvalidRefreshTokenException quand le token est introuvable")
        void shouldThrowWhenTokenNotFound() {
            when(refreshTokenRepository.findByTokenWithUser("unknown"))
                .thenReturn(Optional.empty());

            assertThatThrownBy(() -> refreshTokenService.validateAndRotate("unknown"))
                .isInstanceOf(InvalidRefreshTokenException.class)
                .hasMessageContaining("introuvable");
        }

        @Test
        @DisplayName("Lève InvalidRefreshTokenException quand le token est révoqué")
        void shouldThrowWhenTokenRevoked() {
            validToken.setRevoked(true);
            when(refreshTokenRepository.findByTokenWithUser("valid-token"))
                .thenReturn(Optional.of(validToken));

            assertThatThrownBy(() -> refreshTokenService.validateAndRotate("valid-token"))
                .isInstanceOf(InvalidRefreshTokenException.class)
                .hasMessageContaining("révoqué");
        }

        @Test
        @DisplayName("Lève InvalidRefreshTokenException quand le token est expiré")
        void shouldThrowWhenTokenExpired() {
            validToken.setExpiresAt(Instant.now().minus(1, ChronoUnit.DAYS));
            when(refreshTokenRepository.findByTokenWithUser("valid-token"))
                .thenReturn(Optional.of(validToken));

            assertThatThrownBy(() -> refreshTokenService.validateAndRotate("valid-token"))
                .isInstanceOf(InvalidRefreshTokenException.class)
                .hasMessageContaining("expiré");
        }
    }

    @Nested
    @DisplayName("revoke()")
    class Revoke {

        @Test
        @DisplayName("Révoque le token quand il existe")
        void shouldRevokeToken() {
            when(refreshTokenRepository.findByToken("valid-token"))
                .thenReturn(Optional.of(validToken));

            refreshTokenService.revoke("valid-token");

            assertThat(validToken.isRevoked()).isTrue();
        }

        @Test
        @DisplayName("Ne fait rien quand le token est introuvable (logout idempotent)")
        void shouldDoNothingWhenTokenNotFound() {
            when(refreshTokenRepository.findByToken("unknown"))
                .thenReturn(Optional.empty());

            refreshTokenService.revoke("unknown");

            verify(refreshTokenRepository).findByToken("unknown");
        }
    }
}