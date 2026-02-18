package com.jordanrobin.financial_erp.domain.auth.role;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleName {
    SUPER_ADMIN("Accès total cross-tenant"),
    TENANT_ADMIN("Gestion complète du tenant"),
    ACCOUNTANT("Comptabilité lecture/écriture"),
    ACCOUNTANT_SENIOR("Comptabilité + approbation"),
    CFO("Lecture globale + exports financiers"),
    AUDITOR("Lecture seule + audit logs"),
    VIEWER("Lecture seule basique");

    private final String description;
}
