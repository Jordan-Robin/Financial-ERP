package com.jordanrobin.financial_erp.api.user;

import com.jordanrobin.financial_erp.api.user.dtos.CreateUserRequest;
import com.jordanrobin.financial_erp.api.user.dtos.UserResponse;
import com.jordanrobin.financial_erp.api.user.mappers.UserMapper;
import com.jordanrobin.financial_erp.domain.auth.role.Role;
import com.jordanrobin.financial_erp.domain.auth.role.RoleName;
import com.jordanrobin.financial_erp.domain.auth.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Mapping des utilisateurs (UserMapper)")
class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    @DisplayName("Entité -> DTO : doit copier tous les champs de base et transformer les rôles en RoleName")
    void shouldMapUserToUserResponse() {
        Role adminRole = Role.builder()
            .name(RoleName.TENANT_ADMIN)
            .build();

        User user = User.builder()
            .email("test@expert.com")
            .firstName("Clément")
            .lastName("Dupont")
            .roles(Set.of(adminRole))
            .build();

        UserResponse response = mapper.toResponse(user);

        assertThat(response.id()).isEqualTo(user.getId());
        assertThat(response.email()).isEqualTo(user.getEmail());
        assertThat(response.roles()).containsExactly(RoleName.TENANT_ADMIN);
    }

    @Test
    @DisplayName("DTO -> Entité : doit ignorer le mot de passe lors du mapping (sécurité)")
    void shouldIgnorePasswordOnEntityMapping() {
        CreateUserRequest request = CreateUserRequest.builder()
            .email("test@test.com")
            .password("secret123")
            .roles(Set.of(RoleName.VIEWER))
            .build();

        User entity = mapper.toEntity(request);

        assertThat(entity.getPassword()).isNull();
        assertThat(entity.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("DTO -> Entité : doit générer un UUID v7 automatiquement à la création de l'entité")
    void shouldGenerateIdOnMapping() {
        CreateUserRequest request = CreateUserRequest.builder()
            .email("new@test.com")
            .build();

        User entity = mapper.toEntity(request);

        assertThat(entity.getId()).isNotNull();
        assertThat(entity.getId().version()).isEqualTo(7);
    }
}