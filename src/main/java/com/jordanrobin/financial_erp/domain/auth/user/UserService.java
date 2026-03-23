package com.jordanrobin.financial_erp.domain.auth.user;

import com.jordanrobin.financial_erp.domain.auth.user.mappers.UserDomainMapper;
import com.jordanrobin.financial_erp.domain.auth.user.models.CreateUserCommand;
import com.jordanrobin.financial_erp.domain.auth.user.models.UserResponse;
import com.jordanrobin.financial_erp.domain.auth.role.Role;
import com.jordanrobin.financial_erp.domain.auth.role.RoleService;
import com.jordanrobin.financial_erp.shared.exception.domain.UserExceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserDomainMapper userDomainMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Transactional(readOnly = true)
    public UserResponse getByEmail(String email) {
        return userRepository.findByEmail(email)
            .map(userDomainMapper::entityToResponse)
            .orElseThrow(() -> new UserExceptions.UserNotFoundException(email));
    }

    @Transactional(readOnly = true)
    public UserResponse getById(UUID id) {
        return userRepository.findById(id)
            .map(userDomainMapper::entityToResponse)
            .orElseThrow(() -> new UserExceptions.UserNotFoundException(id.toString()));
    }

    public UserResponse create(CreateUserCommand request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserExceptions.EmailAlreadyExistsException(request.email());
        }
        User user = userDomainMapper.commandToEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));

        Set<Role> roles = request.roles().stream()
            .map(roleService::findByNameOrThrow)
            .collect(Collectors.toSet());
        user.setRoles(roles);

        return userDomainMapper.entityToResponse(userRepository.save(user));
    }

}
