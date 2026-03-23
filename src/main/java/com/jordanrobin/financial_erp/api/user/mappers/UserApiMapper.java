package com.jordanrobin.financial_erp.api.user.mappers;

import com.jordanrobin.financial_erp.api.user.dtos.CreateUserRequest;
import com.jordanrobin.financial_erp.domain.auth.user.models.CreateUserCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserApiMapper {
    CreateUserCommand dtoToCommand(CreateUserRequest request);
}
