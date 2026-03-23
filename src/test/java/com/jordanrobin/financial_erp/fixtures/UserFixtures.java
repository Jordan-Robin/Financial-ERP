package com.jordanrobin.financial_erp.fixtures;

import com.jordanrobin.financial_erp.api.user.dtos.CreateUserRequest;
import com.jordanrobin.financial_erp.domain.auth.role.Role;
import com.jordanrobin.financial_erp.domain.auth.user.User;
import com.jordanrobin.financial_erp.domain.auth.user.models.CreateUserCommand;
import com.jordanrobin.financial_erp.domain.auth.user.models.UserResponse;
import com.jordanrobin.financial_erp.domain.auth.role.RoleName;

import java.util.Set;
import java.util.UUID;

public class UserFixtures {

    static Role adminRole = Role.builder().name(RoleName.SUPER_ADMIN).build();
    static Role viewerRole = Role.builder().name(RoleName.VIEWER).build();

    public static User.UserBuilder adminUserBuilder() {
        return User.builder()
            .email("john@doe.com")
            .firstName("John")
            .lastName("Doe")
            .roles(Set.of(adminRole));
    }

    public static User.UserBuilder viewerUserBuilder() {
        return User.builder()
            .email("john@doe.com")
            .firstName("John")
            .lastName("Doe")
            .roles(Set.of(viewerRole));
    }

    public static UserResponse.UserResponseBuilder adminUserResponseBuilder() {
        return UserResponse.builder()
            .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
            .email("john@doe.com")
            .firstName("John")
            .lastName("Doe")
            .roles(Set.of(RoleName.SUPER_ADMIN));
    }

    public static UserResponse.UserResponseBuilder viewerUserResponseBuilder() {
        return UserResponse.builder()
            .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
            .email("john@doe.com")
            .firstName("John")
            .lastName("Doe")
            .roles(Set.of(RoleName.VIEWER));
    }

    public static CreateUserRequest.CreateUserRequestBuilder createAdminUserRequestBuilder() {
        return CreateUserRequest.builder()
            .email("john@doe.com")
            .password("secret123")
            .firstName("John")
            .lastName("Doe")
            .roles(Set.of(RoleName.SUPER_ADMIN));
    }

    public static CreateUserRequest.CreateUserRequestBuilder createViewerUserRequestBuilder() {
        return CreateUserRequest.builder()
            .email("john@doe.com")
            .password("secret123")
            .firstName("John")
            .lastName("Doe")
            .roles(Set.of(RoleName.VIEWER));
    }

    public static CreateUserCommand.CreateUserCommandBuilder createAdminUserCommandBuilder() {
        return CreateUserCommand.builder()
            .email("john@doe.com")
            .password("secret123")
            .firstName("John")
            .lastName("Doe")
            .roles(Set.of(RoleName.SUPER_ADMIN));
    }

    public static CreateUserCommand.CreateUserCommandBuilder createViewerUserCommandBuilder() {
        return CreateUserCommand.builder()
            .email("john@doe.com")
            .password("secret123")
            .firstName("John")
            .lastName("Doe")
            .roles(Set.of(RoleName.VIEWER));
    }

}
