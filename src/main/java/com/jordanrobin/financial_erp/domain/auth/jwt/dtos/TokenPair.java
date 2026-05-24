package com.jordanrobin.financial_erp.domain.auth.jwt.dtos;

import lombok.Builder;

@Builder
public record TokenPair(
    String accessToken,
    String refreshToken
) {}
