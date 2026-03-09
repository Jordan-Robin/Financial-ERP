package com.jordanrobin.financial_erp.api.auth;

import com.jordanrobin.financial_erp.api.auth.dtos.AuthResponse;
import com.jordanrobin.financial_erp.api.auth.dtos.LoginRequest;
import com.jordanrobin.financial_erp.api.auth.dtos.RefreshRequest;
import com.jordanrobin.financial_erp.config.SecurityConfig;
import com.jordanrobin.financial_erp.domain.auth.AuthService;
import com.jordanrobin.financial_erp.domain.auth.user.CustomUserDetailsService;
import com.jordanrobin.financial_erp.shared.exception.domain.AuthExceptions.InvalidRefreshTokenException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
@AutoConfigureRestTestClient
class AuthControllerTest {

    @Autowired
    private RestTestClient restTestClient;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    private static final AuthResponse MOCK_AUTH_RESPONSE = new AuthResponse(
        "access-token",
        "refresh-token",
        "Bearer",
        900L
    );

    // ===================== LOGIN =====================

    @Test
    void login_shouldReturn200WithTokens_whenCredentialsValid() {
        when(authService.login(any())).thenReturn(MOCK_AUTH_RESPONSE);

        restTestClient.post().uri("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(new LoginRequest("test@test.com", "password"))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.accessToken").isEqualTo("access-token")
            .jsonPath("$.refreshToken").isEqualTo("refresh-token")
            .jsonPath("$.type").isEqualTo("Bearer")
            .jsonPath("$.expiresIn").isEqualTo(900);
    }

    @Test
    void login_shouldReturn401_whenCredentialsInvalid() {
        when(authService.login(any())).thenThrow(new BadCredentialsException("Credentials invalides"));

        restTestClient.post().uri("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(new LoginRequest("test@test.com", "wrong-password"))
            .exchange()
            .expectStatus().isUnauthorized();
    }

    @Test
    void login_shouldReturn400_whenEmailBlank() {
        restTestClient.post().uri("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(new LoginRequest("", "password"))
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void login_shouldReturn400_whenEmailInvalid() {
        restTestClient.post().uri("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(new LoginRequest("not-an-email", "password"))
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void login_shouldReturn400_whenPasswordBlank() {
        restTestClient.post().uri("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(new LoginRequest("test@test.com", ""))
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void login_shouldReturn400_whenBodyMissing() {
        restTestClient.post().uri("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest();
    }

    // ===================== REFRESH =====================

    @Test
    void refresh_shouldReturn200WithNewTokens_whenRefreshTokenValid() {
        when(authService.refresh(any())).thenReturn(MOCK_AUTH_RESPONSE);

        restTestClient.post().uri("/api/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .body(new RefreshRequest("valid-refresh-token"))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.accessToken").isEqualTo("access-token")
            .jsonPath("$.refreshToken").isEqualTo("refresh-token")
            .jsonPath("$.type").isEqualTo("Bearer")
            .jsonPath("$.expiresIn").isEqualTo(900);
    }

    @Test
    void refresh_shouldReturn401_whenRefreshTokenInvalid() {
        when(authService.refresh(any()))
            .thenThrow(new InvalidRefreshTokenException("Refresh token invalide"));

        restTestClient.post().uri("/api/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .body(new RefreshRequest("invalid-token"))
            .exchange()
            .expectStatus().isUnauthorized();
    }

    @Test
    void refresh_shouldReturn401_whenRefreshTokenExpired() {
        when(authService.refresh(any()))
            .thenThrow(new InvalidRefreshTokenException("Refresh token expiré"));

        restTestClient.post().uri("/api/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .body(new RefreshRequest("expired-token"))
            .exchange()
            .expectStatus().isUnauthorized();
    }

    @Test
    void refresh_shouldReturn400_whenRefreshTokenBlank() {
        restTestClient.post().uri("/api/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .body(new RefreshRequest(""))
            .exchange()
            .expectStatus().isBadRequest();
    }

    // ===================== LOGOUT =====================

    @Test
    void logout_shouldReturn204_whenRefreshTokenValid() {
        doNothing().when(authService).logout(any());

        restTestClient.post().uri("/api/auth/logout")
            .contentType(MediaType.APPLICATION_JSON)
            .body(new RefreshRequest("valid-refresh-token"))
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    void logout_shouldReturn401_whenRefreshTokenInvalid() {
        doThrow(new InvalidRefreshTokenException("Refresh token invalide"))
            .when(authService).logout(any());

        restTestClient.post().uri("/api/auth/logout")
            .contentType(MediaType.APPLICATION_JSON)
            .body(new RefreshRequest("invalid-token"))
            .exchange()
            .expectStatus().isUnauthorized();
    }

    @Test
    void logout_shouldReturn400_whenRefreshTokenBlank() {
        restTestClient.post().uri("/api/auth/logout")
            .contentType(MediaType.APPLICATION_JSON)
            .body(new RefreshRequest(""))
            .exchange()
            .expectStatus().isBadRequest();
    }

    // ===================== PROTECTION DES ROUTES =====================

    @Test
    void protectedRoute_shouldReturn401_whenNoToken() {
        restTestClient.post().uri("/api/users")
            .exchange()
            .expectStatus().isUnauthorized();
    }

    @Test
    void protectedRoute_shouldReturn401_whenTokenInvalid() {
        restTestClient.post().uri("/api/users")
            .header("Authorization", "Bearer invalid.jwt.token")
            .exchange()
            .expectStatus().isUnauthorized();
    }
}