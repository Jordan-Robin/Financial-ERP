package com.jordanrobin.financial_erp.api.user;

import com.jordanrobin.financial_erp.api.user.mappers.UserApiMapper;
import com.jordanrobin.financial_erp.api.user.mappers.UserApiMapperImpl;
import com.jordanrobin.financial_erp.base.BaseControllerTest;
import com.jordanrobin.financial_erp.domain.auth.user.CustomUserDetailsService;
import com.jordanrobin.financial_erp.domain.auth.user.UserService;
import com.jordanrobin.financial_erp.domain.auth.user.models.CreateUserCommand;
import com.jordanrobin.financial_erp.infrastructure.security.SecurityConfig;
import com.jordanrobin.financial_erp.shared.exception.domain.UserExceptions;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.util.UUID;

import static com.jordanrobin.financial_erp.fixtures.JwtTokenFixtures.createTenantAdminToken;
import static com.jordanrobin.financial_erp.fixtures.UserFixtures.createViewerUserRequestBuilder;
import static com.jordanrobin.financial_erp.fixtures.UserFixtures.viewerUserResponseBuilder;
import static com.jordanrobin.financial_erp.utils.JsonUtils.fromPath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, UserApiMapperImpl.class})
@DisplayName("UserController")
class UserControllerTest extends BaseControllerTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Nested
    @DisplayName("POST /api/users - create()")
    class CreateUser {

        @Test
        @DisplayName("Succès : retourne 201")
        @SneakyThrows
        void shouldReturn201_whenValid() {
            var request = createViewerUserRequestBuilder().build();
            var response = viewerUserResponseBuilder().build();
            when(userService.create(any(CreateUserCommand.class))).thenReturn(response);

            var result = post("/api/users", createTenantAdminToken(), request);

            assertThat(result)
                .hasStatus(201)
                .bodyJson()
                .returns("john@doe.com", fromPath("$.email"))
                .returns("John", fromPath("$.firstName"));
        }

        @Test
        @DisplayName("Erreur 400 : format d'email invalide")
        @SneakyThrows
        void shouldReturn400_whenEmailInvalid() {
            var request = createViewerUserRequestBuilder().email("bad-email").build();

            var result = post("/api/users", createTenantAdminToken(), request);

            assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
            verifyNoInteractions(userService);
        }

        @Test
        @DisplayName("Erreur 409 : email déjà utilisé")
        @SneakyThrows
        void shouldReturn409_whenEmailAlreadyExists() {
            var request = createViewerUserRequestBuilder().build();
            when(userService.create(any()))
                .thenThrow(new UserExceptions.EmailAlreadyExistsException("john@doe.com"));

            var result = post("/api/users", createTenantAdminToken(), request);

            assertThat(result).hasStatus(HttpStatus.CONFLICT);
        }
    }

    @Nested
    @DisplayName("GET /api/users/{id} - findById()")
    class GetUser {

        @Test
        @DisplayName("Succès : retourne 200")
        void shouldReturn200_whenAuthenticated() {
            var response = viewerUserResponseBuilder().build();
            when(userService.getById(any(UUID.class))).thenReturn(response);

            var result = get("/api/users/{id}", createTenantAdminToken(), UUID.randomUUID());

            assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .returns("john@doe.com", fromPath("$.email"))
                .returns("John", fromPath("$.firstName"));
        }

        @Test
        @DisplayName("Erreur 404 : Utilisateur introuvable")
        void shouldReturn404_whenUserNotFound() {
            UUID unknownId = UUID.randomUUID();
            when(userService.getById(any(UUID.class)))
                .thenThrow(new UserExceptions.UserNotFoundException(unknownId.toString()));

            var result = get("/api/users/{id}",createTenantAdminToken(), unknownId);

            assertThat(result).hasStatus(HttpStatus.NOT_FOUND);
        }
    }
}