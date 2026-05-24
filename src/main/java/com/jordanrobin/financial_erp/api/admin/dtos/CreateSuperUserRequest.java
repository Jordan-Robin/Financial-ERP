package com.jordanrobin.financial_erp.api.admin.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateSuperUserRequest(
    @Email
    @NotBlank
    String email
) {}
