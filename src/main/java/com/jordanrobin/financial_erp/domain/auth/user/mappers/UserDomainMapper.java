package com.jordanrobin.financial_erp.domain.auth.user.mappers;

import com.jordanrobin.financial_erp.domain.auth.role.Role;
import com.jordanrobin.financial_erp.domain.auth.role.RoleName;
import com.jordanrobin.financial_erp.domain.auth.user.User;
import com.jordanrobin.financial_erp.domain.auth.user.models.CreateUserCommand;
import com.jordanrobin.financial_erp.domain.auth.user.models.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "Spring")
public interface UserDomainMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User commandToEntity(CreateUserCommand createUserCommand);

    UserResponse entityToResponse(User user);

    default Set<RoleName> rolesToRoleNames(Set<Role> roles) {
        if (roles == null) return Set.of();
        return roles.stream()
            .map(Role::getName)
            .collect(Collectors.toUnmodifiableSet());
    }
}
