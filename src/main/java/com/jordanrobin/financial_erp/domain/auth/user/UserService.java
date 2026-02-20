package com.jordanrobin.financial_erp.domain.auth.user;

import com.jordanrobin.financial_erp.api.user.dtos.CreateUserRequest;
import com.jordanrobin.financial_erp.api.user.dtos.UserResponse;
import com.jordanrobin.financial_erp.api.user.mappers.UserMapper;
import com.jordanrobin.financial_erp.domain.auth.role.Role;
import com.jordanrobin.financial_erp.domain.auth.role.RoleRepository;
import com.jordanrobin.financial_erp.shared.exception.domain.RoleExceptions;
import com.jordanrobin.financial_erp.shared.exception.domain.UserExceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public UserResponse getByEmail(String email) {
        return userRepository.findByEmail(email)
            .map(userMapper::toResponse)
            .orElseThrow(() -> new UserExceptions.UserNotFoundException(email));
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        return userRepository.findById(id)
            .map(userMapper::toResponse)
            .orElseThrow(() -> new UserExceptions.UserNotFoundException(id.toString()));
    }

    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserExceptions.EmailAlreadyExistsException(request.email());
        }
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));

        Set<Role> roles = request.roles().stream()
            .map(name -> roleRepository.findByName(name)
                .orElseThrow(() -> new RoleExceptions.RoleNotFoundException(name)))
            .collect(Collectors.toSet());
        user.setRoles(roles);

        return userMapper.toResponse(userRepository.save(user));
    }

}
