package com.jordanrobin.financial_erp.domain.auth;

import com.jordanrobin.financial_erp.api.auth.dtos.AuthResponse;
import com.jordanrobin.financial_erp.api.auth.dtos.LoginRequest;
import com.jordanrobin.financial_erp.domain.auth.token.RefreshToken;
import com.jordanrobin.financial_erp.domain.auth.token.RefreshTokenService;
import com.jordanrobin.financial_erp.domain.auth.token.TokenService;
import com.jordanrobin.financial_erp.domain.auth.user.CustomUserDetails;
import com.jordanrobin.financial_erp.domain.auth.user.CustomUserDetailsService;
import com.jordanrobin.financial_erp.domain.auth.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService customUserDetailsService;

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new IllegalStateException("Le principal d'authentification est invalide ou absent");
        }
        String accessToken = tokenService.generateAccessToken(authentication);
        String refreshToken = refreshTokenService.create(userDetails.getUser()).getToken();

        return new AuthResponse(
            accessToken,
            refreshToken,
            "Bearer",
            TokenService.getAccessTokenExpirySeconds()
        );
    }

    public AuthResponse refresh(String refreshToken) {
        // TODO implémenter mesures de sécurité en cas de vol du refresh token
        RefreshToken validToken = refreshTokenService.validateAndRotate(refreshToken);
        User user = validToken.getUser();

        CustomUserDetails userDetails = new CustomUserDetails(
            user,
            customUserDetailsService.getAuthorities(user)
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );

        String newAccessToken = tokenService.generateAccessToken(authentication);
        String newRefreshToken = refreshTokenService.create(user).getToken();

        return new AuthResponse(
            newAccessToken,
            newRefreshToken,
            "Bearer",
            TokenService.getAccessTokenExpirySeconds()
        );
    }

    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }
}
