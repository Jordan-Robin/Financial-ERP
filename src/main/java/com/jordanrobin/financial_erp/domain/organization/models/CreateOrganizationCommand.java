package com.jordanrobin.financial_erp.domain.organization.models;

import com.jordanrobin.financial_erp.domain.organization.LegalStatus;
import lombok.Builder;

import java.time.MonthDay;

@Builder
public record CreateOrganizationCommand(
    String name,
    LegalStatus legalStatus,
    String siren,
    String nafCode,
    MonthDay fiscalYearEndDate
) {}