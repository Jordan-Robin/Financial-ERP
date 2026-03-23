package com.jordanrobin.financial_erp.domain.auth.privilege;

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

    public Privilege save(Privilege privilege) {
        return privilegeRepository.save(privilege);
    }
}
