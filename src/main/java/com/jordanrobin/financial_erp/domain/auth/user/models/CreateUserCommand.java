package com.jordanrobin.financial_erp.domain.auth.user.models;

import com.jordanrobin.financial_erp.domain.auth.role.RoleName;
import lombok.Builder;

import java.util.Set;

@Builder
public record CreateUserCommand(
    String email,
    String password,
    String firstName,
    String lastName,
    Set<RoleName> roles
){}
