package com.jordanrobin.financial_erp.domain.organization.dtos;

import com.jordanrobin.financial_erp.domain.organization.LegalStatus;
import lombok.Builder;

import java.time.MonthDay;
import java.util.UUID;

@Builder
public record CreateOrganizationCommand(
    String name,
    UUID tenantId,
    LegalStatus legalStatus,
    String siren,
    String nafCode,
    MonthDay fiscalYearEndDate
) {}