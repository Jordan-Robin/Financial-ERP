package com.jordanrobin.financial_erp.domain.organization.mappers;

import com.jordanrobin.financial_erp.domain.organization.Organization;
import com.jordanrobin.financial_erp.domain.organization.dtos.CreateOrganizationCommand;
import com.jordanrobin.financial_erp.domain.organization.dtos.OrganizationOutput;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrganizationDomainMapper {

    Organization commandToEntity(CreateOrganizationCommand createOrganizationCommand);

    OrganizationOutput entityToOutput(Organization organization);

}
