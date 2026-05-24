package com.jordanrobin.financial_erp.api.organization.mappers;

import com.jordanrobin.financial_erp.api.organization.dtos.CreateOrganizationRequest;
import com.jordanrobin.financial_erp.domain.organization.dtos.CreateOrganizationCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrganizationApiMapper {
    CreateOrganizationCommand dtoToCommand(CreateOrganizationRequest request);
}
