package com.jordanrobin.financial_erp.domain.auth.role;

import com.jordanrobin.financial_erp.shared.exception.domain.RoleExceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role findByNameOrThrow(RoleName roleName) {
        return roleRepository.findByName(roleName)
            .orElseThrow(() -> new RoleExceptions.RoleNotFoundException(roleName.name()));
    }

    public Optional<Role> findByName(RoleName roleName) {
        return roleRepository.findByName(roleName);
    }

    public Role createRole(RoleName roleName) {
        return Role.builder()
            .name(roleName)
            .description(roleName.getDescription())
            .build();
    }

    public Role save(Role role) {
        return roleRepository.save(role);
    }
}
