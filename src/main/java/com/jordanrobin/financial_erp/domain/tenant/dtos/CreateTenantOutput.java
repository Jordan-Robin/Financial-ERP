package com.jordanrobin.financial_erp.domain.tenant.dtos;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateTenantOutput(
    UUID tenantId,
    UUID organizationId,
    String slug,
    String schemaName
) {}
