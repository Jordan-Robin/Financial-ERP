package com.jordanrobin.financial_erp.api.auth;

import com.jordanrobin.financial_erp.api.auth.dtos.AuthResponse;
import com.jordanrobin.financial_erp.api.auth.dtos.LoginRequest;
import com.jordanrobin.financial_erp.api.auth.dtos.RefreshRequest;
import com.jordanrobin.financial_erp.api.auth.mappers.AuthApiMapper;
import com.jordanrobin.financial_erp.domain.auth.AuthService;
import com.jordanrobin.financial_erp.domain.auth.token.model.TokenPair;
import com.jordanrobin.financial_erp.infrastructure.security.JwtProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Gestion de l'authentification JWT")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtProperties jwtProperties;
    private final AuthApiMapper authApiMapper;

    @Operation(summary = "Connexion", description = "Retourne un access token et un refresh token")
    @ApiResponse(responseCode = "200", description = "Authentification réussie")
    @ApiResponse(responseCode = "401", description = "Credentials invalides")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        TokenPair response = authService.login(request.email(), request.password());

        return ResponseEntity.ok(authApiMapper
            .tokenPairToAuthResponse(response, jwtProperties.tokenType(), jwtProperties.accessTokenExpirySeconds())
        );
    }

    @Operation(summary = "Rafraîchir le token", description = "Retourne un nouvel access token via le refresh token")
    @ApiResponse(responseCode = "200", description = "Token rafraîchi")
    @ApiResponse(responseCode = "401", description = "Refresh token invalide ou expiré")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody @Valid RefreshRequest request) {
        TokenPair response = authService.refresh(request.refreshToken());

        return ResponseEntity.ok(authApiMapper
            .tokenPairToAuthResponse(response, jwtProperties.tokenType(), jwtProperties.accessTokenExpirySeconds())
        );    }

    @Operation(summary = "Déconnexion", description = "Révoque le refresh token")
    @ApiResponse(responseCode = "204", description = "Déconnexion réussie")
    @ApiResponse(responseCode = "401", description = "Refresh token invalide")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid RefreshRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }
}