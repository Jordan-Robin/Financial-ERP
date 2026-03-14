package com.jordanrobin.financial_erp.domain.auth.token;

import com.jordanrobin.financial_erp.domain.auth.user.CustomUserDetails;
import com.jordanrobin.financial_erp.domain.auth.user.User;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TokenService")
class TokenServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private TokenService tokenService;

    private Authentication authentication;

    @BeforeEach
    void setUp() {
        User user = User.builder()
            .email("test@test.com")
            .password("encoded_password")
            .build();

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
            when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt());

            String token = tokenService.generateAccessToken(authentication);

            assertThat(token).isEqualTo("mocked.jwt.token");
        }

        @Test
        @DisplayName("Encode les bons claims : email, subject, scope, issuer")
        void shouldEncodeCorrectClaims() {
            when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt());

            tokenService.generateAccessToken(authentication);

            verify(jwtEncoder).encode(argThat(params -> {
                var claims = params.getClaims();
                return "test@test.com".equals(claims.getClaim("email"))
                    && "1".equals(claims.getSubject())
                    && claims.getClaim("scope").toString().contains("ROLE_USER")
                    && "https://financial-erp.com".equals(claims.getIssuer().toString());
            }));
        }

        @Test
        @DisplayName("Définit une expiration de 900 secondes")
        void shouldSetExpirationTo900Seconds() {
            when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt());

            tokenService.generateAccessToken(authentication);

            verify(jwtEncoder).encode(argThat(params -> {
                Instant issuedAt = params.getClaims().getIssuedAt();
                Instant expiresAt = params.getClaims().getExpiresAt();
                long diff = expiresAt.getEpochSecond() - issuedAt.getEpochSecond();
                return diff == 900L;
            }));
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

    @Nested
    @DisplayName("getAccessTokenExpirySeconds()")
    class GetAccessTokenExpirySeconds {

        @Test
        @DisplayName("Retourne 900 secondes (15 minutes)")
        void shouldReturn900() {
            assertThat(TokenService.getAccessTokenExpirySeconds()).isEqualTo(900L);
        }
    }
}