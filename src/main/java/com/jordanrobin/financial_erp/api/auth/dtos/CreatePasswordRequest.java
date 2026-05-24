package com.jordanrobin.financial_erp.api.auth.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreatePasswordRequest(
    @NotBlank
    String invitationToken,

    @NotBlank @Size(min = 8, message = "Le mot de passe doit contenir au minimum 8 caractères.")
    String password
) {}
