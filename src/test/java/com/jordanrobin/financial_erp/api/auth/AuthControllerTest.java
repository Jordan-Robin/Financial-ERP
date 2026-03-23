package com.jordanrobin.financial_erp.api.auth;

import com.jordanrobin.financial_erp.api.auth.dtos.RefreshRequest;
import com.jordanrobin.financial_erp.api.auth.mappers.AuthApiMapperImpl;
import com.jordanrobin.financial_erp.base.BaseControllerTest;
import com.jordanrobin.financial_erp.domain.auth.AuthService;
import com.jordanrobin.financial_erp.domain.auth.user.CustomUserDetailsService;
import com.jordanrobin.financial_erp.infrastructure.security.JwtProperties;
import com.jordanrobin.financial_erp.infrastructure.security.SecurityConfig;
import com.jordanrobin.financial_erp.shared.exception.domain.AuthExceptions.InvalidRefreshTokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.jordanrobin.financial_erp.fixtures.AuthenticationFixtures.*;
import static com.jordanrobin.financial_erp.utils.JsonUtils.fromPath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, AuthApiMapperImpl.class})
@AutoConfigureRestTestClient
@DisplayName("AuthController")
class AuthControllerTest extends BaseControllerTest {

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private JwtProperties jwtProperties;

    @Nested
    @DisplayName("POST /api/auth/login - login()")
    class LoginTests {

        @Test
        @DisplayName("Succès : Retourne 200 et les tokens")
        void shouldReturn200_whenValid() {
            var request = loginRequestBuilder().build();
            var response = createTokenPair().build();
            when(authService.login(request.email(), request.password())).thenReturn(response);
            when(jwtProperties.tokenType()).thenReturn("Bearer");
            when(jwtProperties.accessTokenExpirySeconds()).thenReturn(3600L);

            var result = postUnauthenticated("/api/auth/login", request);

            assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .returns(response.accessToken(), fromPath("$.accessToken"))
                .returns(response.refreshToken(), fromPath("$.refreshToken"))
                .returns("Bearer", fromPath("$.type"));
        }

        @Test
        @DisplayName("Erreur 401 : Identifiants incorrects")
        void login_shouldReturn401_whenCredentialsInvalid() {
            var request = loginRequestBuilder().build();
            when(authService.login(request.email(), request.password()))
                .thenThrow(new BadCredentialsException("Credentials invalides"));

            var result = postUnauthenticated("/api/auth/login", request);

            assertThat(result).hasStatus(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @DisplayName("Erreur 400 : email et mot de passe vides")
        void login_shouldReturn400_whenFieldsInvalid() {
            var request = loginRequestBuilder().email("").password("").build();

            var result = postUnauthenticated("/api/auth/login", request);

            assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("POST /api/auth/refresh - refresh()")
    class RefreshTests {

        @Test
        @DisplayName("Succès : retourne 200 et de nouveaux tokens")
        void refresh_shouldReturn200_whenTokenValid() {
            var request = refreshRequestBuilder().build();
            var response = createTokenPair().build();
            when(authService.refresh(request.refreshToken())).thenReturn(response);
            when(jwtProperties.tokenType()).thenReturn("Bearer");
            when(jwtProperties.accessTokenExpirySeconds()).thenReturn(3600L);

            var result = postUnauthenticated("/api/auth/refresh", request);

            assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .returns(response.accessToken(), fromPath("$.accessToken"))
                .returns(response.refreshToken(), fromPath("$.refreshToken"));
        }

        @Test
        @DisplayName("Erreur 401 : refreshToken invalide")
        void refresh_shouldReturn401_whenTokenInvalid() {
            when(authService.refresh(any()))
                .thenThrow(new InvalidRefreshTokenException("Token invalide ou expiré"));

            var result = postUnauthenticated("/api/auth/refresh", new RefreshRequest("expired-token"));

            assertThat(result).hasStatus(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @DisplayName("Erreur 400 : refreshToken absent")
        void refresh_shouldReturn400_whenTokenBlank() {
            assertThat(postUnauthenticated("/api/auth/refresh", new RefreshRequest("")))
                .hasStatus(HttpStatus.BAD_REQUEST);
            verify(authService, never()).refresh(anyString());
        }
    }

    @Nested
    @DisplayName("POST /api/auth/logout - logout()")
    class LogoutTests {

        @Test
        @DisplayName("Succès : retourne 204 (idempotent)")
        void logout_shouldReturn204() {
            var request = refreshRequestBuilder().build();

            var result = postUnauthenticated("/api/auth/logout", request);

            assertThat(result).hasStatus(HttpStatus.NO_CONTENT);
            verify(authService).logout(request.refreshToken());
        }

        @Test
        @DisplayName("Erreur 400 : refreshToken absent")
        void logout_shouldReturn400_whenTokenBlank() {
            var result = postUnauthenticated("/api/auth/logout", new RefreshRequest(""));

            assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
            verify(authService, never()).logout(anyString());
        }
    }

    @Nested
    @DisplayName("Protection des ressources (JWT Security)")
    class SecurityTests {

        @Test
        @DisplayName("Erreur 401: token absent")
        void shouldReturn401_whenNoToken() {
            assertThat(get("/api/users/me", null)).hasStatus(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @DisplayName("Erreur 401: token invalide")
        void shouldReturn401_whenTokenMalformed() {
            var result = mvc.get().uri("/api/users/me")
                .header("Authorization", "Bearer invalid-structure")
                .exchange();

            assertThat(result).hasStatus(HttpStatus.UNAUTHORIZED);
        }
    }
}