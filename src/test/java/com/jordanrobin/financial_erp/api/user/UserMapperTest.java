package com.jordanrobin.financial_erp.api.user;

import com.jordanrobin.financial_erp.api.user.dtos.CreateUserRequest;
import com.jordanrobin.financial_erp.api.user.dtos.UserResponse;
import com.jordanrobin.financial_erp.api.user.mappers.UserMapper;
import com.jordanrobin.financial_erp.domain.auth.role.Role;
import com.jordanrobin.financial_erp.domain.auth.user.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void shouldMapUserToUserResponse() {
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");

        User user = User.builder()
            .id(1L)
            .email("test@expert.com")
            .firstName("Clément")
            .lastName("Dupont")
            .roles(Set.of(adminRole))
            .build();

        UserResponse response = mapper.toResponse(user);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.roles()).containsExactly("ROLE_ADMIN");
    }

    @Test
    void shouldIgnorePasswordOnEntityMapping() {
        CreateUserRequest request = CreateUserRequest.builder()
            .email("test@test.com")
            .password("secret123")
            .build();

        User entity = mapper.toEntity(request);

        assertThat(entity.getPassword()).isNull();
    }
}
