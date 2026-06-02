package com.jordanrobin.financial_erp.domain.auth.user.mappers;

import com.jordanrobin.financial_erp.domain.auth.user.User;
import com.jordanrobin.financial_erp.domain.auth.user.dtos.CreateSuperUserCommand;
import com.jordanrobin.financial_erp.domain.auth.user.dtos.UserOutput;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SuperUserDomainMapper {

    User commandToEntity(CreateSuperUserCommand command);

    UserOutput entityToResult(User user);
}
