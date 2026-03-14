package com.jordanrobin.financial_erp.api.user;

import com.jordanrobin.financial_erp.api.user.dtos.CreateUserRequest;
import com.jordanrobin.financial_erp.api.user.dtos.UserResponse;
import com.jordanrobin.financial_erp.config.SecurityConfig;
import com.jordanrobin.financial_erp.domain.auth.role.RoleName;
import com.jordanrobin.financial_erp.domain.auth.user.CustomUserDetailsService;
import com.jordanrobin.financial_erp.domain.auth.user.UserService;
import com.jordanrobin.financial_erp.shared.exception.domain.UserExceptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
@DisplayName("Contrôleur Utilisateurs (UserController)")
class UserControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    private final UUID mockUserId = UUID.randomUUID();

    private final UserResponse MOCK_USER_RESPONSE = new UserResponse(
        mockUserId,
        "john@doe.com",
        "John",
        "Doe",
        Set.of(RoleName.TENANT_ADMIN)
    );

    private CreateUserRequest validCreateRequest() {
        return CreateUserRequest.builder()
            .email("john@doe.com")
            .password("secret123")
            .firstName("John")
            .lastName("Doe")
            .roles(Set.of(RoleName.TENANT_ADMIN))
            .build();
    }

    @Nested
    @DisplayName("Création d'utilisateur - POST /api/users")
    class CreateUser {

        @Test
        @DisplayName("Succes : Retourne 201 avec l'utilisateur créé")
        void shouldReturn201_whenValid() throws Exception {
            when(userService.create(any(CreateUserRequest.class))).thenReturn(MOCK_USER_RESPONSE);

            assertThat(mvc.post().uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest()))
                .with(jwt().jwt(j -> j.claim("scope", "ROLE_ADMIN"))))
                .hasStatus(201)
                .bodyJson()
                .hasPathSatisfying("$.email", email ->
                    assertThat(email).isEqualTo("john@doe.com"))
                .hasPathSatisfying("$.firstName", firstName ->
                    assertThat(firstName).isEqualTo("John"));
        }

        @Test
        @DisplayName("Erreur 400 : Email invalide - le service n'est pas appelé")
        void shouldReturn400_whenEmailInvalid() throws Exception {
            assertThat(mvc.post().uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    CreateUserRequest.builder()
                        .email("pas-un-email")
                        .password("secret123")
                        .firstName("John")
                        .lastName("Doe")
                        .roles(Set.of(RoleName.VIEWER))
                        .build()
                ))
                .with(jwt().jwt(j -> j.claim("scope", "ROLE_ADMIN"))))
                .hasStatus(400);

            verifyNoInteractions(userService);
        }

        @Test
        @DisplayName("Erreur 400 : Body absent")
        void shouldReturn400_whenBodyMissing() {
            assertThat(mvc.post().uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().jwt(j -> j.claim("scope", "ROLE_ADMIN"))))
                .hasStatus(400);

            verifyNoInteractions(userService);
        }

        @Test
        @DisplayName("Erreur 409 : Email déjà utilisé")
        void shouldReturn409_whenEmailAlreadyExists() throws Exception {
            when(userService.create(any()))
                .thenThrow(new UserExceptions.EmailAlreadyExistsException("john@doe.com"));

            assertThat(mvc.post().uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest()))
                .with(jwt().jwt(j -> j.claim("scope", "ROLE_ADMIN"))))
                .hasStatus(409);
        }

        @Test
        @DisplayName("Erreur 401 : Acces refuse sans token")
        void shouldReturn401_whenNotAuthenticated() throws Exception {
            assertThat(mvc.post().uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest())))
                .hasStatus(HttpStatus.UNAUTHORIZED);

            verifyNoInteractions(userService);
        }
    }

    @Nested
    @DisplayName("Récupération d'utilisateur - GET /api/users/{id}")
    class GetUser {

        @Test
        @DisplayName("Succes : Retourne 200 avec l'utilisateur quand JWT valide")
        void shouldReturn200_whenAuthenticated() {
            when(userService.getById(mockUserId)).thenReturn(MOCK_USER_RESPONSE);

            assertThat(mvc.get().uri("/api/users/{id}", mockUserId)
                .with(jwt().jwt(j -> j
                    .subject(mockUserId.toString())
                    .claim("scope", "ROLE_USER")
                )))
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.email", email ->
                    assertThat(email).isEqualTo("john@doe.com"))
                .hasPathSatisfying("$.firstName", firstName ->
                    assertThat(firstName).isEqualTo("John"));
        }

        @Test
        @DisplayName("Erreur 401 : Acces refuse sans token")
        void shouldReturn401_whenNotAuthenticated() {
            assertThat(mvc.get().uri("/api/users/{id}", mockUserId))
                .hasStatus(HttpStatus.UNAUTHORIZED);

            verifyNoInteractions(userService);
        }

        @Test
        @DisplayName("Erreur 404 : Utilisateur introuvable")
        void shouldReturn404_whenUserNotFound() {
            UUID unknownId = UUID.randomUUID();
            when(userService.getById(unknownId))
                .thenThrow(new UserExceptions.UserNotFoundException(unknownId.toString()));

            assertThat(mvc.get().uri("/api/users/{id}", unknownId)
                .with(jwt().jwt(j -> j.subject(unknownId.toString()))))
                .hasStatus(HttpStatus.NOT_FOUND);
        }
    }
}