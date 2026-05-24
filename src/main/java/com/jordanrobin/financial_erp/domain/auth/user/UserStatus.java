package com.jordanrobin.financial_erp.domain.auth.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {
    ACTIVE("En activité"),
    SUSPENDED("Compte suspendu : connexion interdite"),
    PENDING("E-mail non validé");

    private final String description;
}
