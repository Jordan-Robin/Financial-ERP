package com.jordanrobin.financial_erp.domain.auth.token;

import com.jordanrobin.financial_erp.domain.auth.user.CustomUserDetails;
import com.jordanrobin.financial_erp.domain.auth.user.User;
import com.jordanrobin.financial_erp.infrastructure.security.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.jordanrobin.financial_erp.fixtures.JwtTokenFixtures.createTenantAdminToken;
import static com.jordanrobin.financial_erp.fixtures.UserFixtures.adminUserBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TokenService")
class TokenServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private TokenService tokenService;

    private Authentication authentication;
    private final UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @BeforeEach
    void setUp() {
        User user = adminUserBuilder().build();
        user.setId(userId);

        CustomUserDetails userDetails = new CustomUserDetails(
            user,
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );
    }

    @Nested
    @DisplayName("generateAccessToken()")
    class GenerateAccessToken {

        private Jwt mockJwt() {
            return Jwt.withTokenValue("mocked.jwt.token")
                .header("alg", "RS256")
                .claim("sub", "1")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(900))
                .build();
        }

        @Test
        @DisplayName("Retourne la valeur du token généré par le JwtEncoder")
        void shouldReturnTokenValue() {
            var jwt = createTenantAdminToken();
            when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);
            when(jwtProperties.issuer()).thenReturn("issuer");
            when(jwtProperties.accessTokenExpirySeconds()).thenReturn(3600L);

            String token = tokenService.generateAccessToken(authentication);

            assertThat(token).isEqualTo("mock-token");
        }

        @Test
        @DisplayName("Lève IllegalStateException quand le principal n'est pas un CustomUserDetails")
        void shouldThrowWhenPrincipalIsNotCustomUserDetails() {
            Authentication invalidAuth = new UsernamePasswordAuthenticationToken(
                "just-a-string", null, List.of()
            );

            assertThatThrownBy(() -> tokenService.generateAccessToken(invalidAuth))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("invalide");
        }
    }
}