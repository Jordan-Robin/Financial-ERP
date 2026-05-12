package com.jordanrobin.financial_erp.domain.tenant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TenantStatus {
    ACTIVE("Actif"),
    SUSPENDED("Tenant suspendu");

    private final String description;
}
