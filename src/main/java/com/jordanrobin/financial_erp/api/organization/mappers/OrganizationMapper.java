package com.jordanrobin.financial_erp.api.organization.mappers;

import com.jordanrobin.financial_erp.api.organization.dtos.CreateOrganizationRequest;
import com.jordanrobin.financial_erp.api.organization.dtos.OrganizationResponse;
import com.jordanrobin.financial_erp.domain.organization.Organization;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {

    Organization toEntity(CreateOrganizationRequest request);

    OrganizationResponse toResponse(Organization organization);
}
