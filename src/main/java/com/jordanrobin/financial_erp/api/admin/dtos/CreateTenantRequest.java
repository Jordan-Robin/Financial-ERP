package com.jordanrobin.financial_erp.api.admin.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateTenantRequest(
    @NotBlank String tenantSlug,
    @NotBlank String organizationName
) {}
