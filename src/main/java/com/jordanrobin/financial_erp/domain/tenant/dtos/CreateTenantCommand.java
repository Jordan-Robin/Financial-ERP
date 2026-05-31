package com.jordanrobin.financial_erp.domain.tenant.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateTenantCommand(
    String tenantSlug,
    @NotBlank String organizationName
) {}
