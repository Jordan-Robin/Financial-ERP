package com.jordanrobin.financial_erp.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.jordanrobin.financial_erp.domain.auth.privilege.Privilege;
import com.jordanrobin.financial_erp.domain.auth.privilege.PrivilegeName;
import com.jordanrobin.financial_erp.domain.auth.privilege.PrivilegeRepository;
import com.jordanrobin.financial_erp.domain.auth.role.Role;
import com.jordanrobin.financial_erp.domain.auth.role.RoleName;
import com.jordanrobin.financial_erp.domain.auth.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.jordanrobin.financial_erp.domain.auth.privilege.PrivilegeName.*;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class RolePrivilegeInitializer implements ApplicationRunner {

    private final PrivilegeRepository privilegeRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(@NonNull ApplicationArguments args) {
        // 1. Upsert des privilèges
        Map<PrivilegeName, Privilege> privileges = Arrays.stream(PrivilegeName.values())
            .map(pn -> privilegeRepository.findByName(pn.name())
                .orElseGet(() -> privilegeRepository.save(
                    Privilege.builder().name(pn.name()).description(pn.getDescription()).build())))
            .collect(Collectors.toMap(
                p -> PrivilegeName.valueOf(p.getName()), p -> p));

        // 2. Upsert des rôles avec leurs privilèges
        upsertRole(RoleName.SUPER_ADMIN, new HashSet<>(privileges.values()));

        upsertRole(RoleName.TENANT_ADMIN, allExcept(privileges, TENANT_SETTINGS_WRITE, PRIVILEGE_READ));

        upsertRole(RoleName.VIEWER, Set.of(
            privileges.get(USER_READ),
            privileges.get(ROLE_READ)
        ));
    }

    private void upsertRole(RoleName roleName, Set<Privilege> privileges) {
        Role role = roleRepository.findByName(roleName.name())
            .orElseGet(() -> Role.builder()
                .name(roleName.name())
                .description(roleName.getDescription())
                .build());
        if (!role.getPrivileges().equals(privileges)) {
            role.setPrivileges(new HashSet<>(privileges));
            roleRepository.save(role);
        }
    }

    private Set<Privilege> allExcept(Map<PrivilegeName, Privilege> privileges, PrivilegeName... excluded) {
        Set<PrivilegeName> excludedSet = Set.of(excluded);
        return privileges.entrySet().stream()
            .filter(e -> !excludedSet.contains(e.getKey()))
            .map(Map.Entry::getValue)
            .collect(Collectors.toSet());
    }
}
