package com.jordanrobin.financial_erp.domain.auth.user.models;

import com.jordanrobin.financial_erp.domain.auth.role.RoleName;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record UserResponse(
    UUID id,
    String email,
    String firstName,
    String lastName,
    Set<RoleName> roles
) {}
