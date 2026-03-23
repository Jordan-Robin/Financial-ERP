package com.jordanrobin.financial_erp.domain.organization.models;

import com.jordanrobin.financial_erp.domain.organization.LegalStatus;
import lombok.Builder;

import java.time.MonthDay;
import java.util.UUID;

@Builder
public record OrganizationResponse(
    UUID id,
    String name,
    LegalStatus legalStatus,
    String siren,
    String nafCode,
    MonthDay fiscalYearEndDate
) {}
