package com.jordanrobin.financial_erp.domain.auth.user.dtos;

import lombok.Builder;

@Builder
public record CreateUserCommand(
    String email
){}
