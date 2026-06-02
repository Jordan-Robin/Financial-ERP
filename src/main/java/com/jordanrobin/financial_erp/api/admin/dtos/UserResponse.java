package com.jordanrobin.financial_erp.api.admin.dtos;

import com.jordanrobin.financial_erp.domain.auth.user.UserStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserResponse(
    UUID id,
    String email,
    UserStatus status
) {}
