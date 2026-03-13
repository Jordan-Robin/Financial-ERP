package com.jordanrobin.financial_erp.domain.auth;

import com.jordanrobin.financial_erp.api.auth.dtos.AuthResponse;
import com.jordanrobin.financial_erp.api.auth.dtos.LoginRequest;
import com.jordanrobin.financial_erp.domain.auth.token.RefreshToken;
import com.jordanrobin.financial_erp.domain.auth.token.RefreshTokenService;
import com.jordanrobin.financial_erp.domain.auth.token.TokenService;
import com.jordanrobin.financial_erp.domain.auth.user.*;
import com.jordanrobin.financial_erp.shared.exception.domain.AuthExceptions.InvalidRefreshTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
        user = User.builder()
            .id(1L)
            .email("test@test.com")
            .build();

        CustomUserDetails userDetails = new CustomUserDetails(
            user,
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );

        refreshToken = RefreshToken.builder()
            .id(1L)
            .token("refresh-token")
            .user(user)
            .revoked(false)
            .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
            .build();
    }

    @Test
    void login_shouldReturnAuthResponse() {
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(tokenService.generateAccessToken(authentication)).thenReturn("access-token");
        when(refreshTokenService.create(user)).thenReturn(refreshToken);

        AuthResponse response = authService.login(new LoginRequest("test@test.com", "password"));

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.type()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(900L);
    }

    @Test
    void login_shouldThrowWhenCredentialsInvalid() {
        when(authenticationManager.authenticate(any()))
            .thenThrow(new BadCredentialsException("Credentials invalides"));

        assertThatThrownBy(() -> authService.login(new LoginRequest("test@test.com", "wrong")))
            .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_shouldCallAuthenticationManagerWithCorrectCredentials() {
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(tokenService.generateAccessToken(any())).thenReturn("access-token");
        when(refreshTokenService.create(any())).thenReturn(refreshToken);

        authService.login(new LoginRequest("test@test.com", "password"));

        verify(authenticationManager).authenticate(
            argThat(auth ->
                auth.getPrincipal().equals("test@test.com") &&
                    auth.getCredentials().equals("password")
            )
        );
    }

    @Test
    void refresh_shouldReturnNewAuthResponse() {
        when(refreshTokenService.validateAndRotate("refresh-token")).thenReturn(refreshToken);
        when(customUserDetailsService.getAuthorities(user))
            .thenReturn(List.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(tokenService.generateAccessToken(any())).thenReturn("new-access-token");
        when(refreshTokenService.create(user)).thenReturn(
            RefreshToken.builder().token("new-refresh-token").build()
        );

        AuthResponse response = authService.refresh("refresh-token");

        assertThat(response.accessToken()).isEqualTo("new-access-token");
        assertThat(response.refreshToken()).isEqualTo("new-refresh-token");
        assertThat(response.type()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(900L);
    }

    @Test
    void refresh_shouldThrowWhenRefreshTokenInvalid() {
        when(refreshTokenService.validateAndRotate("invalid-token"))
            .thenThrow(new InvalidRefreshTokenException("Token invalide"));

        assertThatThrownBy(() -> authService.refresh("invalid-token"))
            .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    void logout_shouldRevokeRefreshToken() {
        doNothing().when(refreshTokenService).revoke("refresh-token");

        authService.logout("refresh-token");

        verify(refreshTokenService).revoke("refresh-token");
    }
}
