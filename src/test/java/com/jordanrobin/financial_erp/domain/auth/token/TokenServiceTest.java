package com.jordanrobin.financial_erp.domain.auth.token;

import com.jordanrobin.financial_erp.domain.auth.user.CustomUserDetails;
import com.jordanrobin.financial_erp.domain.auth.user.User;
import org.junit.jupiter.api.BeforeEach;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private TokenService tokenService;

    private User user;
    private CustomUserDetails userDetails;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .id(1L)
            .email("test@test.com")
            .password("encoded_password")
            .build();

        userDetails = new CustomUserDetails(
            user,
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );
    }

    @Test
    void generateAccessToken_shouldReturnTokenValue() {
        Jwt jwt = Jwt.withTokenValue("mocked.jwt.token")
            .header("alg", "RS256")
            .claim("sub", "1")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(900))
            .build();

        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        String token = tokenService.generateAccessToken(authentication);

        assertThat(token).isEqualTo("mocked.jwt.token");
    }

    @Test
    void generateAccessToken_shouldEncodeCorrectClaims() {
        Jwt jwt = Jwt.withTokenValue("mocked.jwt.token")
            .header("alg", "RS256")
            .claim("sub", "1")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(900))
            .build();

        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        tokenService.generateAccessToken(authentication);

        verify(jwtEncoder).encode(argThat(params -> {
            var claims = params.getClaims();
            return claims.getClaim("email").equals("test@test.com")
                && claims.getSubject().equals("1")
                && claims.getClaim("scope").toString().contains("ROLE_USER")
                && claims.getIssuer().toString().equals("https://financial-erp.com");
        }));
    }

    @Test
    void generateAccessToken_shouldSetExpirationTo900Seconds() {
        Jwt jwt = Jwt.withTokenValue("mocked.jwt.token")
            .header("alg", "RS256")
            .claim("sub", "1")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(900))
            .build();

        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        tokenService.generateAccessToken(authentication);

        verify(jwtEncoder).encode(argThat(params -> {
            Instant issuedAt = params.getClaims().getIssuedAt();
            Instant expiresAt = params.getClaims().getExpiresAt();
            long diff = expiresAt.getEpochSecond() - issuedAt.getEpochSecond();
            return diff == 900L;
        }));
    }

    @Test
    void generateAccessToken_shouldThrowWhenPrincipalIsNotCustomUserDetails() {
        Authentication invalidAuth = new UsernamePasswordAuthenticationToken(
            "just-a-string", null, List.of()
        );

        assertThatThrownBy(() -> tokenService.generateAccessToken(invalidAuth))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("invalide");
    }

    @Test
    void getAccessTokenExpirySeconds_shouldReturn900() {
        assertThat(TokenService.getAccessTokenExpirySeconds()).isEqualTo(900L);
    }
}
