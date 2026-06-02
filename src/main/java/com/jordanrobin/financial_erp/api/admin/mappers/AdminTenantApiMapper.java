package com.jordanrobin.financial_erp.api.admin.mappers;

import com.jordanrobin.financial_erp.api.admin.dtos.CreateTenantRequest;
import com.jordanrobin.financial_erp.api.admin.dtos.CreateTenantResponse;
import com.jordanrobin.financial_erp.domain.tenant.dtos.CreateTenantCommand;
import com.jordanrobin.financial_erp.domain.tenant.dtos.CreateTenantOutput;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminTenantApiMapper {

    CreateTenantCommand requestToCommand(CreateTenantRequest request);

    CreateTenantResponse outputToResponse(CreateTenantOutput output);
}
