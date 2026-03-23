package com.jordanrobin.financial_erp.api.auth.dtos;

import lombok.Builder;

@Builder
public record AuthResponse(
    String accessToken,
    String refreshToken,
    String type,
    long expiresIn
) {}
