package com.jordanrobin.financial_erp.domain.auth;

import com.jordanrobin.financial_erp.domain.auth.token.RefreshToken;
import com.jordanrobin.financial_erp.domain.auth.token.RefreshTokenService;
import com.jordanrobin.financial_erp.domain.auth.token.TokenService;
import com.jordanrobin.financial_erp.domain.auth.token.model.TokenPair;
import com.jordanrobin.financial_erp.domain.auth.user.CustomUserDetails;
import com.jordanrobin.financial_erp.domain.auth.user.CustomUserDetailsService;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static com.jordanrobin.financial_erp.fixtures.JwtTokenFixtures.createRefreshToken;
import static com.jordanrobin.financial_erp.fixtures.UserFixtures.adminUserBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private TokenService tokenService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private AuthService authService;

    private User user;
    private Authentication authentication;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        user = adminUserBuilder().build();

        CustomUserDetails userDetails = new CustomUserDetails(
            user,
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );

        refreshToken = createRefreshToken().user(user).build();
    }

    @Nested
    @DisplayName("login()")
    class Login {

        @Test
        @DisplayName("Succès : retourne un AuthResponse")
        void shouldAuthenticateAndReturnTokens() {
            // Arrange
            String email = "test@test.com";
            String password = "password";
            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(tokenService.generateAccessToken(any())).thenReturn("access-token");
            when(refreshTokenService.create(any())).thenReturn(refreshToken);

            // Act
            TokenPair result = authService.login(email, password);

            // Assert
            assertThat(result.accessToken()).isEqualTo("access-token");
            assertThat(result.refreshToken()).isEqualTo(refreshToken.getToken());

            // On vérifie que l'objet créé à la volée dans la méthode est le bon
            ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

            verify(authenticationManager).authenticate(authCaptor.capture());

            UsernamePasswordAuthenticationToken captured = authCaptor.getValue();
            assertThat(captured.getPrincipal()).isEqualTo(email);
            assertThat(captured.getCredentials()).isEqualTo(password);
        }

        @Test
        @DisplayName("Erreur BadCredentialsException : credentials invalides")
        void shouldThrow_whenCredentialsInvalid() {
            // Arrange
            when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credentials invalides"));

            // Act & Assert
            assertThatThrownBy(() -> authService.login("email", "password"))
                .isInstanceOf(BadCredentialsException.class);
        }
    }

    @Nested
    @DisplayName("refresh()")
    class Refresh {

        @Test
        @DisplayName("Succès : retourne AuthResponse")
        void shouldReturnNewAuthResponse() {
            // Arrange
            when(refreshTokenService.validateAndRotate("refresh-token")).thenReturn(refreshToken);
            when(customUserDetailsService.getAuthorities(user))
                .thenReturn(List.of(new SimpleGrantedAuthority("ROLE_USER")));
            when(tokenService.generateAccessToken(any())).thenReturn("new-access-token");
            when(refreshTokenService.create(user)).thenReturn(
                RefreshToken.builder().token("new-refresh-token").build()
            );

            // Act
            TokenPair result = authService.refresh("refresh-token");

            // Assert
            assertThat(result.accessToken()).isEqualTo("new-access-token");
            assertThat(result.refreshToken()).isEqualTo("new-refresh-token");
        }

        @Test
        @DisplayName("Erreur InvalidRefreshTokenException si refresh token invalide")
        void shouldThrow_whenRefreshTokenIsInvalid() {
            // Arrange
            when(refreshTokenService.validateAndRotate("invalid-token"))
                .thenThrow(new InvalidRefreshTokenException("Token invalide"));

            // Act & Assert
            assertThatThrownBy(() -> authService.refresh("invalid-token"))
                .isInstanceOf(InvalidRefreshTokenException.class);
        }
    }

    @Nested
    @DisplayName("logout()")
    class Logout {

        @Test
        @DisplayName("Succès : révoque le refresh token")
        void shouldRevokeRefreshToken() {
            // Arrange
            doNothing().when(refreshTokenService).revoke("refresh-token");

            // Act
            authService.logout("refresh-token");

            // Assert
            verify(refreshTokenService).revoke("refresh-token");
        }
    }
}