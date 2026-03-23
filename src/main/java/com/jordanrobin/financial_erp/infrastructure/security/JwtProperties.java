package com.jordanrobin.financial_erp.infrastructure.security;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.security.jwt")
@Validated
public record JwtProperties(
    @NotNull Long accessTokenExpirySeconds,
    @NotNull Long refreshTokenExpirySeconds,
    @NotNull String issuer,
    @NotNull String tokenType
) {}
