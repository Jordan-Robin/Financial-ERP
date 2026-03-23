package com.jordanrobin.financial_erp.api.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequest(
    @NotBlank @Email String email,
    @NotBlank String password
) {}
