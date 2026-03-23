package com.jordanrobin.financial_erp.domain.auth.token.model;

import lombok.Builder;

@Builder
public record TokenPair(
    String accessToken,
    String refreshToken
) {}
