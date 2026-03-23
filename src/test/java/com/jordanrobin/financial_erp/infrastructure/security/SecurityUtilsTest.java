package com.jordanrobin.financial_erp.infrastructure.security;

import com.jordanrobin.financial_erp.domain.auth.user.CustomUserDetails;
import com.jordanrobin.financial_erp.domain.auth.user.User;
import com.jordanrobin.financial_erp.shared.exception.api.SecurityExceptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityUtils - getCurrentUser()")
class SecurityUtilsTest {

    private final SecurityUtils securityUtils = new SecurityUtils();

    @Mock
    private SecurityContext securityContext;

    @Test
    @DisplayName("Retourne l'utilisateur courant quand l'authentification est valide")
    void getCurrentUser_shouldReturnUser_whenAuthenticated() {
        User user = User.builder()
            .email("test@example.com")
            .password("hash")
            .build();

        CustomUserDetails userDetails = new CustomUserDetails(user, List.of());
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, List.of());

        given(securityContext.getAuthentication()).willReturn(auth);

        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            User result = securityUtils.getCurrentUser();

            assertThat(result.getEmail()).isEqualTo("test@example.com");
        }
    }

    @Test
    @DisplayName("Lève UnauthorizedException quand aucune authentification n'est présente")
    void getCurrentUser_shouldThrow_whenNotAuthenticated() {
        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            given(securityContext.getAuthentication()).willReturn(null);
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            assertThatThrownBy(securityUtils::getCurrentUser)
                .isInstanceOf(SecurityExceptions.UnauthorizedException.class)
                .hasMessage("No authenticated user found.");
        }
    }

    @Test
    @DisplayName("Lève UnauthorizedException quand le principal n'est pas un CustomUserDetails")
    void getCurrentUser_shouldThrow_whenPrincipalIsNotCustomUserDetails() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
            "anonymousUser", null, List.of()
        );

        given(securityContext.getAuthentication()).willReturn(auth);

        try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
            mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            assertThatThrownBy(securityUtils::getCurrentUser)
                .isInstanceOf(SecurityExceptions.UnauthorizedException.class)
                .hasMessage("No authenticated user found.");
        }
    }
}
