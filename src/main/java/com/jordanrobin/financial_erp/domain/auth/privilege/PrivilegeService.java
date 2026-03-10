package com.jordanrobin.financial_erp.domain.auth.privilege;

import com.jordanrobin.financial_erp.shared.exception.domain.PrivilegeExceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrivilegeService {

    private final PrivilegeRepository privilegeRepository;

    public Optional<Privilege> findByName(PrivilegeName name) {
        return privilegeRepository.findByName(name);
    }

    public Privilege findByNameOrThrow(PrivilegeName name) {
        return privilegeRepository.findByName(name)
            .orElseThrow(() -> new PrivilegeExceptions.PrivilegeNotFoundException(name.name()));
    }

    public Privilege save(Privilege privilege) {
        return privilegeRepository.save(privilege);
    }
}
