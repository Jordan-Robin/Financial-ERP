package com.jordanrobin.financial_erp.api.auth.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RefreshRequest(@NotBlank String refreshToken) {}
