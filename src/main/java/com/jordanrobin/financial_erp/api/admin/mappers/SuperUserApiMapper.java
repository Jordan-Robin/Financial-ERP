package com.jordanrobin.financial_erp.api.admin.mappers;

import com.jordanrobin.financial_erp.api.admin.dtos.CreateSuperUserRequest;
import com.jordanrobin.financial_erp.api.admin.dtos.SuperUserResponse;
import com.jordanrobin.financial_erp.domain.auth.user.dtos.CreateSuperUserCommand;
import com.jordanrobin.financial_erp.domain.auth.user.dtos.UserOutput;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SuperUserApiMapper {
    CreateSuperUserCommand requestToCommand(CreateSuperUserRequest request);

    SuperUserResponse outputToResponse(UserOutput output);
}
