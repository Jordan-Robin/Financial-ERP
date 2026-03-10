package com.jordanrobin.financial_erp.api.auth.dtos;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String type,
    long expiresIn
) {}
