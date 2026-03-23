package com.jordanrobin.financial_erp.fixtures;

import com.jordanrobin.financial_erp.api.organization.dtos.CreateOrganizationRequest;
import com.jordanrobin.financial_erp.domain.organization.Organization;
import com.jordanrobin.financial_erp.domain.organization.models.CreateOrganizationCommand;
import com.jordanrobin.financial_erp.domain.organization.models.OrganizationResponse;
import com.jordanrobin.financial_erp.domain.organization.LegalStatus;

import java.time.MonthDay;
import java.util.UUID;

public class OrganizationFixtures {

    public static Organization.OrganizationBuilder createOrganization() {
        return Organization.builder()
            .name("Organization")
            .legalStatus(LegalStatus.SA)
            .siren("123456789")
            .nafCode("0000A")
            .fiscalYearEndDate(MonthDay.of(12, 31));
    }

    public static CreateOrganizationRequest.CreateOrganizationRequestBuilder createOrganizationRequestBuilder() {
        return CreateOrganizationRequest.builder()
            .name("Organization")
            .legalStatus(LegalStatus.SA)
            .siren("123456789")
            .nafCode("0000A")
            .fiscalYearEndDate(MonthDay.of(12, 31));
    }

    public static CreateOrganizationCommand.CreateOrganizationCommandBuilder createOrganizationCommandBuilder() {
        return CreateOrganizationCommand.builder()
            .name("Organization")
            .legalStatus(LegalStatus.SA)
            .siren("123456789")
            .nafCode("0000A")
            .fiscalYearEndDate(MonthDay.of(12, 31));
    }

    public static OrganizationResponse.OrganizationResponseBuilder organizationResponseBuilder() {
        return OrganizationResponse.builder()
            .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
            .name("Organization")
            .legalStatus(LegalStatus.SA)
            .siren("123456789")
            .nafCode("0000A")
            .fiscalYearEndDate(MonthDay.of(12, 31));
    }

}
