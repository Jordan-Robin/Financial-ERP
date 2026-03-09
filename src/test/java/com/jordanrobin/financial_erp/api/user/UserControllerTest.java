package com.jordanrobin.financial_erp.api.user;

import com.jordanrobin.financial_erp.api.user.dtos.CreateUserRequest;
import com.jordanrobin.financial_erp.api.user.dtos.UserResponse;
import com.jordanrobin.financial_erp.config.SecurityConfig;
import com.jordanrobin.financial_erp.domain.auth.role.RoleName;
import com.jordanrobin.financial_erp.domain.auth.user.CustomUserDetailsService;
import com.jordanrobin.financial_erp.domain.auth.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
@AutoConfigureRestTestClient
class UserControllerTest {

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_shouldReturn201() {
        CreateUserRequest request = CreateUserRequest.builder()
            .email("john@doe.com")
            .password("secret123")
            .firstName("John")
            .lastName("Doe")
            .roles(Set.of(RoleName.TENANT_ADMIN))
            .build();

        UserResponse response = new UserResponse(1L, "john@doe.com", "John", "Doe", Set.of(RoleName.TENANT_ADMIN));

        when(userService.create(any(CreateUserRequest.class))).thenReturn(response);

        restTestClient.post().uri("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .body(objectMapper.writeValueAsString(request))
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.id").isEqualTo(1L)
            .jsonPath("$.email").isEqualTo("john@doe.com");
    }

    @Test
    @WithMockUser // Par défaut ROLE_USER
    void getUser_shouldReturn200() {
        UserResponse response = new UserResponse(1L, "john@doe.com", "John", "Doe", Set.of(RoleName.VIEWER));
        when(userService.getById(1L)).thenReturn(response);

        restTestClient.get().uri("/api/users/1")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.firstName").isEqualTo("John");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_shouldReturn400_whenEmailInvalid() {
        CreateUserRequest invalidRequest = CreateUserRequest.builder()
            .email("pas-un-email") // Déclenchera @Email
            .password("123")
            .firstName("J")
            .lastName("D")
            .roles(Set.of(RoleName.VIEWER))
            .build();

        restTestClient.post().uri("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .body(objectMapper.writeValueAsString(invalidRequest))
            .exchange()
            .expectStatus().isBadRequest();

        verifyNoInteractions(userService);
    }
}
