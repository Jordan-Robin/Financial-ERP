package com.jordanrobin.financial_erp.domain.organization.mappers;

import com.jordanrobin.financial_erp.domain.organization.Organization;
import com.jordanrobin.financial_erp.domain.organization.models.CreateOrganizationCommand;
import com.jordanrobin.financial_erp.domain.organization.models.OrganizationResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrganizationDomainMapper {

    Organization commandToEntity(CreateOrganizationCommand createOrganizationCommand);

    OrganizationResponse entityToResponse(Organization organization);

}
