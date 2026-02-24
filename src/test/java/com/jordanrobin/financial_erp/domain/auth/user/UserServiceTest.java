package com.jordanrobin.financial_erp.domain.auth.user;

import com.jordanrobin.financial_erp.api.user.dtos.CreateUserRequest;
import com.jordanrobin.financial_erp.api.user.dtos.UserResponse;
import com.jordanrobin.financial_erp.api.user.mappers.UserMapper;
import com.jordanrobin.financial_erp.domain.auth.role.Role;
import com.jordanrobin.financial_erp.domain.auth.role.RoleRepository;
import com.jordanrobin.financial_erp.shared.exception.domain.RoleExceptions;
import com.jordanrobin.financial_erp.shared.exception.domain.UserExceptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getByEmail_shouldReturnUserResponse_whenUserExists() {
        String email = "john.doe@email.com";
        User user = User.builder().id(1L).email(email).roles(new HashSet<>()).build();
        UserResponse response = UserResponse.builder().id(1L).email(email).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.getByEmail(email);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo(email);
        verify(userRepository).findByEmail(email);
        verify(userMapper).toResponse(user);
    }

    @Test
    void getByEmail_shouldThrowUserNotFoundException_whenUserNotFound() {
        String email = "unknown@email.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getByEmail(email))
            .isInstanceOf(UserExceptions.UserNotFoundException.class)
            .hasMessageContaining(email);
    }

    @Test
    void getById_shouldReturnUserResponse_whenUserExists() {
        Long id = 1L;
        User user = User.builder().id(id).email("john.doe@email.com").roles(new HashSet<>()).build();
        UserResponse response = UserResponse.builder().id(id).email("john.doe@email.com").build();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.getById(id);

        assertThat(result.id()).isEqualTo(id);
        verify(userRepository).findById(id);
        verify(userMapper).toResponse(user);
    }

    @Test
    void getById_shouldThrowUserNotFoundException_whenUserNotFound() {
        Long id = 99L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(id))
            .isInstanceOf(UserExceptions.UserNotFoundException.class)
            .hasMessageContaining(id.toString());
    }

    @Test
    void create_shouldReturnUserResponse_whenSuccess() {
        CreateUserRequest request = CreateUserRequest.builder()
            .email("john.doe@email.com")
            .password("secret")
            .firstName("John")
            .lastName("Doe")
            .roles(Set.of("USER"))
            .build();

        Role role = Role.builder().name("USER").build();
        User user = User.builder().id(1L).email(request.email()).roles(new HashSet<>()).build();
        UserResponse response = UserResponse.builder().id(1L).email(request.email()).build();

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded_secret");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.create(request);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo(request.email());
        verify(passwordEncoder).encode("secret");
        verify(userRepository).save(user);
    }

    @Test
    void create_shouldThrowEmailAlreadyExistsException_whenEmailTaken() {
        CreateUserRequest request = CreateUserRequest.builder()
            .email("john.doe@email.com")
            .password("secret")
            .firstName("John")
            .lastName("Doe")
            .roles(Set.of("USER"))
            .build();

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> userService.create(request))
            .isInstanceOf(UserExceptions.EmailAlreadyExistsException.class)
            .hasMessageContaining(request.email());
    }

    @Test
    void create_shouldThrowRoleNotFoundException_whenRoleDoesNotExist() {
        CreateUserRequest request = CreateUserRequest.builder()
            .email("john.doe@email.com")
            .password("secret")
            .firstName("John")
            .lastName("Doe")
            .roles(Set.of("UNKNOWN_ROLE"))
            .build();

        User user = User.builder().id(1L).email(request.email()).roles(new HashSet<>()).build();

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(roleRepository.findByName("UNKNOWN_ROLE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.create(request))
            .isInstanceOf(RoleExceptions.RoleNotFoundException.class)
            .hasMessageContaining("UNKNOWN_ROLE");
    }
}
