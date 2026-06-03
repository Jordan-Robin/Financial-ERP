package com.jordanrobin.financial_erp.domain.auth.user.dtos;

import com.jordanrobin.financial_erp.domain.auth.role.RoleName;
import com.jordanrobin.financial_erp.domain.auth.user.UserStatus;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record UserResponse(
    UUID id,
    String email,
    UserStatus status
) {}
