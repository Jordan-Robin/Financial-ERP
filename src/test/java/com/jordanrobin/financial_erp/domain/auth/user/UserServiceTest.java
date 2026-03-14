package com.jordanrobin.financial_erp.domain.auth.user;

import com.jordanrobin.financial_erp.api.user.dtos.CreateUserRequest;
import com.jordanrobin.financial_erp.api.user.dtos.UserResponse;
import com.jordanrobin.financial_erp.api.user.mappers.UserMapper;
import com.jordanrobin.financial_erp.domain.auth.role.Role;
import com.jordanrobin.financial_erp.domain.auth.role.RoleName;
import com.jordanrobin.financial_erp.domain.auth.role.RoleService;
import com.jordanrobin.financial_erp.shared.exception.domain.RoleExceptions;
import com.jordanrobin.financial_erp.shared.exception.domain.UserExceptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.jordanrobin.financial_erp.domain.auth.role.RoleName.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

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
        @DisplayName("Retourne un UserResponse quand l'utilisateur existe")
        void shouldReturnUserResponse_whenUserExists() {
            String email = "john.doe@email.com";
            User user = User.builder().email(email).roles(new HashSet<>()).build();
            UserResponse response = UserResponse.builder()
                .id(user.getId())
                .email(email)
                .build();

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(userMapper.toResponse(user)).thenReturn(response);

            UserResponse result = userService.getByEmail(email);

            assertThat(result.email()).isEqualTo(email);
            assertThat(result.id()).isEqualTo(user.getId());
            verify(userRepository).findByEmail(email);
            verify(userMapper).toResponse(user);
        }

        @Test
        @DisplayName("Lève UserNotFoundException quand l'utilisateur est introuvable")
        void shouldThrowUserNotFoundException_whenUserNotFound() {
            String email = "unknown@email.com";
            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getByEmail(email))
                .isInstanceOf(UserExceptions.UserNotFoundException.class)
                .hasMessageContaining(email);
        }
    }

    @Nested
    @DisplayName("getById()")
    class GetById {

        @Test
        @DisplayName("Retourne un UserResponse quand l'utilisateur existe")
        void shouldReturnUserResponse_whenUserExists() {
            User user = User.builder().email("john.doe@email.com").roles(new HashSet<>()).build();
            UserResponse response = UserResponse.builder()
                .id(user.getId())
                .email("john.doe@email.com")
                .build();

            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            when(userMapper.toResponse(user)).thenReturn(response);

            UserResponse result = userService.getById(user.getId());

            assertThat(result.id()).isEqualTo(user.getId());
            verify(userRepository).findById(user.getId());
            verify(userMapper).toResponse(user);
        }

        @Test
        @DisplayName("Lève UserNotFoundException quand l'utilisateur est introuvable")
        void shouldThrowUserNotFoundException_whenUserNotFound() {
            UUID id = UUID.randomUUID(); // corrigé : UUID au lieu de Long
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getById(id))
                .isInstanceOf(UserExceptions.UserNotFoundException.class)
                .hasMessageContaining(id.toString());
        }
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("Retourne un UserResponse quand la création réussit")
        void shouldReturnUserResponse_whenSuccess() {
            RoleName roleName = VIEWER;
            CreateUserRequest request = CreateUserRequest.builder()
                .email("john.doe@email.com")
                .password("secret")
                .firstName("John")
                .lastName("Doe")
                .roles(Set.of(roleName))
                .build();

            Role role = Role.builder().name(VIEWER).build();
            User user = User.builder().email(request.email()).roles(new HashSet<>()).build();
            UserResponse response = UserResponse.builder()
                .id(user.getId())
                .email(request.email())
                .build();

            when(userRepository.existsByEmail(request.email())).thenReturn(false);
            when(userMapper.toEntity(request)).thenReturn(user);
            when(passwordEncoder.encode(request.password())).thenReturn("encoded_secret");
            when(roleService.findByNameOrThrow(roleName)).thenReturn(role);
            when(userRepository.save(user)).thenReturn(user);
            when(userMapper.toResponse(user)).thenReturn(response);

            UserResponse result = userService.create(request);

            assertThat(result.email()).isEqualTo(request.email());
            assertThat(result.id()).isEqualTo(user.getId());
            verify(roleService).findByNameOrThrow(roleName);
            verify(passwordEncoder).encode("secret");
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("Lève EmailAlreadyExistsException quand l'email est déjà utilisé")
        void shouldThrowEmailAlreadyExistsException_whenEmailTaken() {
            CreateUserRequest request = CreateUserRequest.builder()
                .email("john.doe@email.com")
                .password("secret")
                .firstName("John")
                .lastName("Doe")
                .roles(Set.of(VIEWER))
                .build();

            when(userRepository.existsByEmail(request.email())).thenReturn(true);

            assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(UserExceptions.EmailAlreadyExistsException.class)
                .hasMessageContaining(request.email());
        }

        @Test
        @DisplayName("Lève RoleNotFoundException quand le rôle n'existe pas en base")
        void shouldThrowRoleNotFoundException_whenRoleDoesNotExist() {
            CreateUserRequest request = CreateUserRequest.builder()
                .email("john.doe@email.com")
                .password("secret")
                .firstName("John")
                .lastName("Doe")
                .roles(Set.of(CFO))
                .build();

            User user = User.builder().email(request.email()).roles(new HashSet<>()).build();

            when(userRepository.existsByEmail(request.email())).thenReturn(false);
            when(userMapper.toEntity(request)).thenReturn(user);
            when(passwordEncoder.encode(any())).thenReturn("encoded");
            when(roleService.findByNameOrThrow(CFO))
                .thenThrow(new RoleExceptions.RoleNotFoundException(CFO.name()));

            assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(RoleExceptions.RoleNotFoundException.class)
                .hasMessageContaining("CFO");
        }
    }
}