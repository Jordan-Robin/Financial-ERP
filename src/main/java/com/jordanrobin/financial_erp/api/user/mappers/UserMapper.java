package com.jordanrobin.financial_erp.api.user.mappers;

import com.jordanrobin.financial_erp.api.user.dtos.CreateUserRequest;
import com.jordanrobin.financial_erp.api.user.dtos.UpdateUserRequest;
import com.jordanrobin.financial_erp.api.user.dtos.UserResponse;
import com.jordanrobin.financial_erp.domain.auth.role.Role;
import com.jordanrobin.financial_erp.domain.auth.user.User;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toEntity(CreateUserRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    void updateEntity(UpdateUserRequest request, @MappingTarget User user);

    default Set<String> rolesToStrings(Set<Role> roles) {
        if (roles == null) return Set.of();
        return roles.stream()
            .map(Role::getName)
            .collect(Collectors.toUnmodifiableSet());
    }
}
