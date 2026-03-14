package com.jordanrobin.financial_erp.api.auth;

import com.jordanrobin.financial_erp.api.auth.dtos.AuthResponse;
import com.jordanrobin.financial_erp.api.auth.dtos.LoginRequest;
import com.jordanrobin.financial_erp.api.auth.dtos.RefreshRequest;
import com.jordanrobin.financial_erp.config.SecurityConfig;
import com.jordanrobin.financial_erp.domain.auth.AuthService;
import com.jordanrobin.financial_erp.domain.auth.user.CustomUserDetailsService;
import com.jordanrobin.financial_erp.shared.exception.domain.AuthExceptions.InvalidRefreshTokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
@AutoConfigureRestTestClient
@DisplayName("Contrôleur d'Authentification (AuthController)")
class AuthControllerTest {

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private MockMvcTester mvc;

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

    @Nested
    @DisplayName("Identification (Login) - POST /api/auth/login")
    class LoginTests {

        @Test
        @DisplayName("Succes : Retourne 200 et les tokens quand les identifiants sont corrects")
        void login_shouldReturn200_whenValid() {
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
        @DisplayName("Erreur 401 : Identifiants incorrects")
        void login_shouldReturn401_whenCredentialsInvalid() {
            when(authService.login(any()))
                .thenThrow(new BadCredentialsException("Credentials invalides"));

            restTestClient.post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginRequest("test@test.com", "wrong"))
                .exchange()
                .expectStatus().isUnauthorized();
        }

        @Test
        @DisplayName("Erreur 400 : Email vide ou invalide, mot de passe vide")
        void login_shouldReturn400_whenFieldsInvalid() {
            restTestClient.post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginRequest("", ""))
                .exchange()
                .expectStatus().isBadRequest();
        }

        @Test
        @DisplayName("Erreur 400 : Body absent")
        void login_shouldReturn400_whenBodyMissing() {
            restTestClient.post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
        }
    }

    @Nested
    @DisplayName("Renouvellement de token (Refresh) - POST /api/auth/refresh")
    class RefreshTests {

        @Test
        @DisplayName("Succes : Retourne 200 et de nouveaux tokens")
        void refresh_shouldReturn200_whenTokenValid() {
            when(authService.refresh(any())).thenReturn(MOCK_AUTH_RESPONSE);

            restTestClient.post().uri("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RefreshRequest("valid-token"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accessToken").isEqualTo("access-token")
                .jsonPath("$.refreshToken").isEqualTo("refresh-token");
        }

        @Test
        @DisplayName("Erreur 401 : Token de rafraichissement invalide ou expire")
        void refresh_shouldReturn401_whenTokenInvalid() {
            when(authService.refresh(any()))
                .thenThrow(new InvalidRefreshTokenException("Token invalide ou expiré"));

            restTestClient.post().uri("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RefreshRequest("expired-token"))
                .exchange()
                .expectStatus().isUnauthorized();
        }

        @Test
        @DisplayName("Erreur 400 : Token de rafraichissement vide")
        void refresh_shouldReturn400_whenTokenBlank() {
            restTestClient.post().uri("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RefreshRequest(""))
                .exchange()
                .expectStatus().isBadRequest();
        }
    }

    @Nested
    @DisplayName("Deconnexion (Logout) - POST /api/auth/logout")
    class LogoutTests {

        @Test
        @DisplayName("Succes : Retourne 204 (idempotent)")
        void logout_shouldReturn204() {
            doNothing().when(authService).logout(any());

            restTestClient.post().uri("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RefreshRequest("valid-token"))
                .exchange()
                .expectStatus().isNoContent();
        }

        @Test
        @DisplayName("Erreur 400 : Token de rafraichissement vide")
        void logout_shouldReturn400_whenTokenBlank() {
            restTestClient.post().uri("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RefreshRequest(""))
                .exchange()
                .expectStatus().isBadRequest();
        }
    }

    @Nested
    @DisplayName("Protection des ressources (JWT Security)")
    class SecurityTests {

        @Test
        @DisplayName("Accès refuse : Aucun token fourni - 401")
        void shouldReturn401_whenNoToken() {
            restTestClient.get().uri("/api/users/me")
                .exchange()
                .expectStatus().isUnauthorized();
        }

        @Test
        @DisplayName("Accès refuse : Token malforme - 401")
        void shouldReturn401_whenTokenMalformed() {
            restTestClient.get().uri("/api/users/me")
                .header("Authorization", "Bearer invalid-structure")
                .exchange()
                .expectStatus().isUnauthorized();
        }

        @Test
        @DisplayName("Acces autorise : JWT valide simule via jwt() post-processor")
        void shouldAllowAccess_whenJwtIsValid() {
            UUID userId = UUID.randomUUID();

            assertThat(mvc.get().uri("/api/users/me")
                .with(jwt().jwt(j -> j
                    .subject(userId.toString())
                    .claim("scope", "ROLE_USER")
                )))
                .hasStatus(HttpStatus.NOT_FOUND);
        }
    }
}