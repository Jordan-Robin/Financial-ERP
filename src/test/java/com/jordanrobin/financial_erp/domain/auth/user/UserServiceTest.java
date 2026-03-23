package com.jordanrobin.financial_erp.domain.auth.user;

import com.jordanrobin.financial_erp.domain.auth.user.mappers.UserDomainMapper;
import com.jordanrobin.financial_erp.domain.auth.user.models.CreateUserCommand;
import com.jordanrobin.financial_erp.domain.auth.user.models.UserResponse;
import com.jordanrobin.financial_erp.domain.auth.role.Role;
import com.jordanrobin.financial_erp.domain.auth.role.RoleName;
import com.jordanrobin.financial_erp.domain.auth.role.RoleService;
import com.jordanrobin.financial_erp.shared.exception.domain.UserExceptions;
import com.jordanrobin.financial_erp.shared.exception.domain.resources.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static com.jordanrobin.financial_erp.domain.auth.role.RoleName.*;
import static com.jordanrobin.financial_erp.fixtures.UserFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDomainMapper userDomainMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("getByEmail()")
    class GetByEmail {

        @Test
        @DisplayName("Succès : retourne un UserResponse")
        void shouldReturnUserResponse_whenUserExists() {
            // Arrange
            User user = adminUserBuilder().build();
            UserResponse response = adminUserResponseBuilder().build();

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(userDomainMapper.entityToResponse(user)).thenReturn(response);

            // Act
            UserResponse result = userService.getByEmail(user.getEmail());

            // Assert
            assertThat(result.email()).isEqualTo(user.getEmail());
            verify(userRepository).findByEmail(user.getEmail());
            verify(userDomainMapper).entityToResponse(user);
        }

        @Test
        @DisplayName("Erreur UserNotFoundException quand l'utilisateur est introuvable")
        void shouldThrowUserNotFoundException_whenUserNotFound() {
            // Arrange
            String email = "unknown@email.com";
            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> userService.getByEmail(email))
                .isInstanceOf(UserExceptions.UserNotFoundException.class)
                .hasMessageContaining(email);
        }
    }

    @Nested
    @DisplayName("getById()")
    class GetById {

        @Test
        @DisplayName("Succès : retourne un UserResponse")
        void shouldReturnUserResponse_whenUserExists() {
            // Arrange
            User user = adminUserBuilder().build();
            UserResponse response = adminUserResponseBuilder().build();

            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            when(userDomainMapper.entityToResponse(user)).thenReturn(response);

            // Act
            UserResponse result = userService.getById(user.getId());

            // Assert
            assertThat(result.email()).isEqualTo(user.getEmail());
            verify(userRepository).findById(user.getId());
            verify(userDomainMapper).entityToResponse(user);
        }

        @Test
        @DisplayName("Erreur UserNotFoundException quand l'utilisateur est introuvable")
        void shouldThrowUserNotFoundException_whenUserNotFound() {
            // Arrange
            UUID id = UUID.randomUUID();
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> userService.getById(id))
                .isInstanceOf(UserExceptions.UserNotFoundException.class)
                .hasMessageContaining(id.toString());
        }
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("Succès : retourne un UserResponse")
        void shouldReturnUserResponse_whenSuccess() {
            // Arrange
            CreateUserCommand request = createAdminUserCommandBuilder().build();
            User user = adminUserBuilder().build();
            UserResponse response = adminUserResponseBuilder().build();

            when(userRepository.existsByEmail(request.email())).thenReturn(false);
            when(userDomainMapper.commandToEntity(request)).thenReturn(user);
            when(passwordEncoder.encode(request.password())).thenReturn("encoded_secret");
            when(roleService.findByNameOrThrow(any(RoleName.class)))
                .thenReturn(Role.builder().name(SUPER_ADMIN).build());
            when(userRepository.save(user)).thenReturn(user);
            when(userDomainMapper.entityToResponse(user)).thenReturn(response);

            // Act
            UserResponse result = userService.create(request);

            // Assert
            assertThat(result.email()).isEqualTo(request.email());
            verify(roleService).findByNameOrThrow(any());
            verify(passwordEncoder).encode(request.password());
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("Erreur EmailAlreadyExistsException quand l'email est déjà utilisé")
        void shouldThrowEmailAlreadyExistsException_whenEmailTaken() {
            // Arrange
            CreateUserCommand request = createAdminUserCommandBuilder().build();

            when(userRepository.existsByEmail(request.email())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(UserExceptions.EmailAlreadyExistsException.class)
                .hasMessageContaining(request.email());
        }

        @Test
        @DisplayName("Erreur ResourceNotFoundException quand le rôle n'existe pas en base")
        void shouldThrowResourceNotFoundException_whenRoleDoesNotExist() {
            // Arrange
            CreateUserCommand request = createAdminUserCommandBuilder().build();
            User user = adminUserBuilder().build();

            when(userRepository.existsByEmail(request.email())).thenReturn(false);
            when(userDomainMapper.commandToEntity(request)).thenReturn(user);
            when(passwordEncoder.encode(request.password())).thenReturn("encoded_secret");
            when(roleService.findByNameOrThrow(SUPER_ADMIN))
                .thenThrow(new ResourceNotFoundException(Role.class.getSimpleName(), SUPER_ADMIN.name()));

            // Act & Assert
            assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("SUPER_ADMIN");
        }
    }
}