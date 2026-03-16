package com.jordanrobin.financial_erp.api.organization.dtos;

import com.jordanrobin.financial_erp.domain.organization.LegalStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.MonthDay;

public record CreateOrganizationRequest(

    @NotBlank(message = "Le nom est obligatoire.")
    @Size(max = 100, message = "Le nom ne doit pas faire plus de 100 caractères.")
    String name,

    LegalStatus legalStatus,

    @Pattern(regexp = "^\\d{9}$", message = "Le SIREN doit contenir exactement 9 chiffres")
    String siren,

    @Size(max = 10, message = "Le code NAF ne doit pas faire plus de 10 caractères.")
    String nafCode,

    MonthDay fiscalYearEndDate
){}
