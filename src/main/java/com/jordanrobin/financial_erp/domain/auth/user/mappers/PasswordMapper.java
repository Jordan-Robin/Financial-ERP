package com.jordanrobin.financial_erp.domain.auth.user.mappers;

import com.jordanrobin.financial_erp.api.auth.dtos.CreatePasswordRequest;
import com.jordanrobin.financial_erp.domain.auth.user.dtos.CreatePasswordCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PasswordMapper {

    CreatePasswordCommand requestToCommand(CreatePasswordRequest request);
}
