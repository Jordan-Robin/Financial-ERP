package com.jordanrobin.financial_erp.domain.auth.user.dtos;

import com.jordanrobin.financial_erp.domain.auth.user.UserStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record SuperUserResult(
    UUID id,
    String email,
    UserStatus status
) {}
