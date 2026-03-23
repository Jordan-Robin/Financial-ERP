package com.jordanrobin.financial_erp.api.user;

import com.jordanrobin.financial_erp.api.user.dtos.CreateUserRequest;
import com.jordanrobin.financial_erp.api.user.mappers.UserApiMapper;
import com.jordanrobin.financial_erp.domain.auth.user.models.CreateUserCommand;
import com.jordanrobin.financial_erp.domain.auth.role.RoleName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserApiMapper")
class UserApiMapperTest {

    private final UserApiMapper mapper = Mappers.getMapper(UserApiMapper.class);

    @Test
    @DisplayName("Request -> Command : doit mapper fidèlement tous les champs")
    void shouldMapRequestToCommand() {
        CreateUserRequest request = CreateUserRequest.builder()
            .email("clément@expert.com")
            .password("secret123")
            .firstName("Clément")
            .lastName("Dupont")
            .roles(Set.of(RoleName.TENANT_ADMIN))
            .build();

        CreateUserCommand command = mapper.dtoToCommand(request);

        assertThat(command.email()).isEqualTo(request.email());
        assertThat(command.password()).isEqualTo(request.password());
        assertThat(command.firstName()).isEqualTo(request.firstName());
        assertThat(command.lastName()).isEqualTo(request.lastName());
        assertThat(command.roles()).containsExactly(RoleName.TENANT_ADMIN);
    }
}