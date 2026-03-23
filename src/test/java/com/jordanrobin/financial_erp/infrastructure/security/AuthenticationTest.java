package com.jordanrobin.financial_erp.infrastructure.security;

import com.jordanrobin.financial_erp.api.user.UserController;
import com.jordanrobin.financial_erp.api.user.mappers.UserApiMapperImpl;
import com.jordanrobin.financial_erp.base.BaseControllerTest;
import com.jordanrobin.financial_erp.domain.auth.user.CustomUserDetailsService;
import com.jordanrobin.financial_erp.domain.auth.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, UserApiMapperImpl.class})
@DisplayName("Tests d'authentification")
public class AuthenticationTest extends BaseControllerTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("Erreur 401 : Accès refusé sans token")
    void shouldReturn401_whenNotAuthenticated() {
        MvcTestResult result = getUnauthenticated("/api/users/{id}", UUID.randomUUID());

        assertThat(result).hasStatus(HttpStatus.UNAUTHORIZED);
        verifyNoInteractions(userService);
    }
}
