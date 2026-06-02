package com.jordanrobin.financial_erp.api.admin.dtos;

import java.util.UUID;

public record CreateTenantResponse(
    UUID tenantId,
    UUID organizationId,
    String slug,
    String schemaName
) {}
