package com.jordanrobin.financial_erp.api.auth.dtos;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(@NotBlank String refreshToken) {}
