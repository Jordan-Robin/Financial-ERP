package com.jordanrobin.financial_erp.api.user.dtos;

import lombok.Builder;

import java.util.Set;

@Builder
public record UserResponse(
    Long id,
    String email,
    String firstName,
    String lastName,
    Set<String> roles
) {}
